package com.intellij.tasks.lighthouse;

import com.intellij.tasks.TasksIcons;
import com.intellij.tasks.impl.BaseRepositoryImpl;
import consulo.logging.Logger;
import consulo.task.*;
import consulo.ui.image.Image;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.Comparing;
import consulo.util.lang.StringUtil;
import consulo.util.lang.ref.Ref;
import consulo.util.xml.serializer.annotation.Tag;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dennis.Ushakov
 */
@Tag("Lighthouse")
public class LighthouseRepository extends BaseRepositoryImpl {
  private static final Logger LOG = Logger.getInstance("#com.intellij.tasks.lighthouse.LighthouseRepository");
  private static final Pattern DATE_PATTERN = Pattern.compile("(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d).*(\\d\\d:\\d\\d:\\d\\d).*");
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private Pattern myPattern;
  private String myProjectId;
  private String myAPIKey;

  /** for serialization */
  @SuppressWarnings({"UnusedDeclaration"})
  public LighthouseRepository() {
  }

  public LighthouseRepository(TaskRepositoryType type) {
    super(type);
  }

  @Override
  public BaseRepository clone() {
    return new LighthouseRepository(this);
  }

  private LighthouseRepository(LighthouseRepository other) {
    super(other);
    setProjectId(other.myProjectId);
    setAPIKey(myAPIKey = other.myAPIKey);
  }

  @Override
  public void testConnection() throws Exception {
    getIssues(null, 10, 0);
  }

  @Override
  public boolean isConfigured() {
    return super.isConfigured() &&
           StringUtil.isNotEmpty(getProjectId()) &&
           StringUtil.isNotEmpty(getAPIKey());
  }

  @Override
  public Task[] getIssues(@Nullable String query, int max, long since) throws Exception {
    String url = "/projects/" + myProjectId + "/tickets.xml";
    url += "?q=" + encodeUrl("state:open sort:updated ");
    if (!StringUtil.isEmpty(query)) {
      url += encodeUrl(query);
    }
    final List<Task> tasks = new ArrayList<Task>();
    int page = 1;
    final HttpClient client = login();
    while (tasks.size() < max) {
      HttpMethod method = doREST(url + "&page=" + page, false, client);
      InputStream stream = method.getResponseBodyAsStream();
      Element element = new SAXBuilder(false).build(stream).getRootElement();
      if ("nil-classes".equals(element.getName())) break;
      if (!"tickets".equals(element.getName())) {
        LOG.warn("Error fetching issues for: " + url + ", HTTP status code: " + method.getStatusCode());
        throw new Exception("Error fetching issues for: " + url + ", HTTP status code: " + method.getStatusCode() +
                          "\n" + element.getText());
      }

      List<Element> children = element.getChildren("ticket");

      List<Task> taskList = ContainerUtil.mapNotNull(children, o -> createIssue(o));
      tasks.addAll(taskList);
      page++;
    }
    return tasks.toArray(new Task[tasks.size()]);
  }

  @Nullable
  private Task createIssue(Element element) {
    final String id = element.getChildText("number");
    if (id == null) {
      return null;
    }
    final String summary = element.getChildText("title");
    if (summary == null) {
      return null;
    }
    final String description = element.getChildText("original-body");
    final boolean isClosed = "true".equals(element.getChildText("closed"));
    final Ref<Date> updated = new Ref<Date>();
    final Ref<Date> created = new Ref<Date>();
    try {
      updated.set(parseDate(element, "updated-at"));
      created.set(parseDate(element, "created-at"));
    } catch (ParseException e) {
      LOG.warn(e);
    }

    return new Task() {
      @Override
      public boolean isIssue() {
        return true;
      }

      @Override
      public String getIssueUrl() {
        return getUrl() + "/projects/" + myProjectId + "/tickets/" + getId() + ".xml";
      }

      @Nonnull
      @Override
      public String getId() {
        return myProjectId + "-" + id;
      }

      @Nonnull
      @Override
      public String getSummary() {
        return summary;
      }

      public String getDescription() {
        return description;
      }

      @Nonnull
      @Override
      public Comment[] getComments() {
        return new Comment[0];
      }

      @Nonnull
      @Override
      public Image getIcon() {
        return TasksIcons.Lighthouse;
      }

      @Nonnull
      @Override
      public TaskType getType() {
        return TaskType.BUG;
      }

      @Override
      public Date getUpdated() {
        return updated.get();
      }

      @Override
      public Date getCreated() {
        return created.get();
      }

      @Override
      public boolean isClosed() {
        return isClosed;
      }

      @Override
      public TaskRepository getRepository() {
        return LighthouseRepository.this;
      }

      @Override
      public String getPresentableName() {
        return getId() + ": " + getSummary();
      }
    };
  }

  @Nullable
  private static Date parseDate(Element element, String name) throws ParseException {
    final Matcher m = DATE_PATTERN.matcher(element.getChildText(name));
    if (m.find()) {
      return DATE_FORMAT.parse(m.group(1) + " " + m.group(2));
    }
    return null;
  }

  @Nullable
  public String extractId(String taskName) {
    Matcher matcher = myPattern.matcher(taskName);
    return matcher.find() ? matcher.group(1) : null;
  }

  private HttpMethod doREST(String request, boolean post, HttpClient client) throws Exception {
    String uri = getUrl() + request;
    HttpMethod method = post ? new PostMethod(uri) : new GetMethod(uri);
    configureHttpMethod(method);
    client.executeMethod(method);
    return method;
  }

  private HttpClient login() throws Exception {
    HttpClient client = getHttpClient();
    GetMethod method = new GetMethod(getUrl() + "/projects.xml");
    configureHttpMethod(method);
    client.getParams().setContentCharset("UTF-8");
    client.executeMethod(method);
    if (method.getStatusCode() != 200) {
      throw new Exception("Cannot login: HTTP returned " + method.getStatusCode());
    }
    String response = method.getResponseBodyAsString();
    if (response == null) {
      throw new NullPointerException();
    }
    if (response.contains("<errors>")) {
      int pos = response.indexOf("</errors>");
      int length = "<errors>".length();
      if (pos > length) {
        response = response.substring(length, pos);
      }
      throw new Exception("Cannot login: " + response);
    }
    return client;
  }

  @Override
  protected void configureHttpMethod(HttpMethod method) {
    method.addRequestHeader("X-LighthouseToken", myAPIKey);
  }

  @Nullable
  @Override
  public Task findTask(String id) throws Exception {
    final String[] split = id.split("\\-");
    final String projectId = split[0];
    final String realId = split[1];
    if (!Comparing.strEqual(projectId, myProjectId)) return null;
    HttpMethod method = doREST("/projects/" + myProjectId + "/tickets/" + realId +".xml", false, login());
    InputStream stream = method.getResponseBodyAsStream();
    Element element = new SAXBuilder(false).build(stream).getRootElement();
    return element.getName().equals("ticket") ? createIssue(element) : null;
  }

  public String getProjectId() {
    return myProjectId;
  }

  public void setProjectId(String projectId) {
    myProjectId = projectId;
    myPattern = Pattern.compile("(" + projectId + "\\-\\d+):\\s+");
  }

  public String getAPIKey() {
    return myAPIKey;
  }

  public void setAPIKey(String APIKey) {
    myAPIKey = APIKey;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) return false;
    if (!(o instanceof LighthouseRepository)) return false;

    LighthouseRepository that = (LighthouseRepository)o;
    if (getAPIKey() != null ? !getAPIKey().equals(that.getAPIKey()) : that.getAPIKey() != null) return false;
    if (getProjectId() != null ? !getProjectId().equals(that.getProjectId()) : that.getProjectId() != null) return false;
    return true;
  }
}
