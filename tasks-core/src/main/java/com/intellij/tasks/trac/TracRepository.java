/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.tasks.trac;

import com.intellij.tasks.TasksIcons;
import com.intellij.tasks.impl.BaseRepositoryImpl;
import consulo.task.*;
import consulo.ui.image.Image;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.Comparing;
import consulo.util.xml.serializer.annotation.Tag;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Dmitry Avdeev
 */
@Tag("Trac")
public class TracRepository extends BaseRepositoryImpl {
  private static final ThreadLocal<CancelableTransport> ourStaticTransport = new ThreadLocal<>();

  private class CancelableTransport extends XmlRpcCommonsTransport {
    public CancelableTransport(XmlRpcCommonsTransportFactory factory) throws MalformedURLException {
      super(factory);
    }

    void cancel() {
      method.abort();
    }

    @Override
    public Object sendRequest(XmlRpcRequest pRequest) throws XmlRpcException {
      ourStaticTransport.set(this);
      return super.sendRequest(pRequest);
    }

    @Override
    protected void close() throws XmlRpcClientException {
      super.close();
      CancelableTransport cancelableTransport = ourStaticTransport.get();
      if (cancelableTransport == this) {
        ourStaticTransport.remove();
      }
    }
  }

  private static void cancelCurrentRequest() {
    CancelableTransport cancelableTransport = ourStaticTransport.get();
    if (cancelableTransport != null) {
      cancelableTransport.cancel();
    }
  }

  private String myDefaultSearch = "status!=closed&owner={username}&summary~={query}";
  private Boolean myMaxSupported;

  @Override
  public Task[] getIssues(@Nullable String query, int max, long since) throws Exception {
    return getIssues(query, max);
  }

  private Task[] getIssues(@Nullable String query, int max) throws Exception {
    final XmlRpcClient client = getRpcClient();

    List<Object> result = null;
    String search = myDefaultSearch + "&max=" + max;
    if (myMaxSupported == null) {
      try {
        myMaxSupported = true;
        result = runQuery(query, client, search);
      }
      catch (XmlRpcException e) {
        if (e.getMessage().contains("t.max")) {
          myMaxSupported = false;
        }
        else {
          throw e;
        }
      }
    }
    if (!myMaxSupported) {
      search = myDefaultSearch;
    }
    if (result == null) {
      result = runQuery(query, client, search);
    }

    if (result == null) {
      throw new Exception("Cannot connect to " + getUrl());
    }

    ArrayList<Task> tasks = new ArrayList<Task>(max);
    int min = Math.min(max, result.size());
    for (int i = 0; i < min; i++) {
      Task task = getTask((Integer)result.get(i), client);
      ContainerUtil.addIfNotNull(tasks, task);
    }
    return tasks.toArray(new Task[tasks.size()]);
  }

  @SuppressWarnings("unchecked")
  private List<Object> runQuery(@Nullable String query, XmlRpcClient client, String search) throws XmlRpcException, IOException {
    if (query != null) {
      search = search.replace("{query}", query);
    }
    search = search.replace("{username}", getUsername());
    return (List<Object>)client.execute("ticket.query", Arrays.asList(search));
  }

  private XmlRpcClient getRpcClient() throws MalformedURLException {
    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
    config.setServerURL(new URL(getUrl()));
    config.setEncoding("UTF-8");

    XmlRpcClient client = new XmlRpcClient();
    XmlRpcCommonsTransportFactory commonsTransportFactory = new XmlRpcCommonsTransportFactory(client) {
      @Override
      public XmlRpcTransport getTransport() {
        return super.getTransport();
      }
    };
    commonsTransportFactory.setHttpClient(getHttpClient());
    client.setConfig(config);
    client.setTransportFactory(commonsTransportFactory);
    return client;
  }

  @Nullable
  @Override
  public Task findTask(@Nonnull String id) throws Exception {
    return getTask(Integer.parseInt(id), getRpcClient());
  }

  public String getDefaultSearch() {
    return myDefaultSearch;
  }

  public void setDefaultSearch(String defaultSearch) {
    myDefaultSearch = defaultSearch;
  }

  @Nullable
  @SuppressWarnings("unchecked")
  private Task getTask(int id, XmlRpcClient client) throws IOException, XmlRpcException {
    Object response = client.execute("ticket.get", Arrays.asList(id));
    if (response == null) {
      return null;
    }
    final List<Object> vector = (List<Object>)response;
    final Map<String, String> map = (Map<String, String>)vector.get(3);
    return new Task() {

      @Nonnull
      @Override
      public String getId() {
        return vector.get(0).toString();
      }

      @Nonnull
      @Override
      public String getSummary() {
        return map.get("summary");
      }

      @Nullable
      @Override
      public String getDescription() {
        return null;
      }

      @Nonnull
      @Override
      public Comment[] getComments() {
        return new Comment[0];
      }

      @Nonnull
      @Override
      public Image getIcon() {
        return TasksIcons.Trac;
      }

      @Nonnull
      @Override
      public TaskType getType() {
        TaskType taskType = TaskType.OTHER;
        String type = map.get("type");
        if (type == null) {
          return taskType;
        }
        if ("Feature".equals(type) || "enhancement".equals(type)) {
          taskType = TaskType.FEATURE;
        }
        else if ("Bug".equals(type) || "defect".equals(type) || "error".equals(type)) {
          taskType = TaskType.BUG;
        }
        else if ("Exception".equals(type)) {
          taskType = TaskType.EXCEPTION;
        }
        return taskType;
      }

      @Nullable
      @Override
      public Date getUpdated() {
        return getDate(vector.get(2));
      }

      @Nullable
      @Override
      public Date getCreated() {
        return getDate(vector.get(1));
      }

      @Override
      public boolean isClosed() {
        return false;
      }

      @Override
      public boolean isIssue() {
        return true;
      }

      @Nullable
      @Override
      public String getIssueUrl() {
        return null;
      }

      @Nullable
      @Override
      public TaskRepository getRepository() {
        return TracRepository.this;
      }
    };
  }

  private static Date getDate(Object o) {
    return o instanceof Date ? (Date)o : new Date((Integer)o * 1000l);
  }

  @Nullable
  @Override
  public CancellableConnection createCancellableConnection() {
    return new CancellableConnection() {
      @Override
      protected void doTest() throws Exception {
        getIssues("", 1);
      }

      @Override
      public void cancel() {
        cancelCurrentRequest();
      }
    };
  }

  @Nonnull
  @Override
  public BaseRepository clone() {
    return new TracRepository(this);
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public TracRepository() {
    // for serialization
  }

  public TracRepository(TracRepositoryType repositoryType) {
    super(repositoryType);
    setUrl("http://myserver.com/login/rpc");
    myUseHttpAuthentication = true;
  }

  private TracRepository(TracRepository other) {
    super(other);
    myDefaultSearch = other.myDefaultSearch;
  }

  @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass"})
  @Override
  public boolean equals(Object o) {
    return super.equals(o) && Comparing.equal(((TracRepository)o).getDefaultSearch(), getDefaultSearch());
  }

  @Override
  protected int getFeatures() {
    return super.getFeatures() | BASIC_HTTP_AUTHORIZATION;
  }
}
