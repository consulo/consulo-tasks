package com.intellij.tasks.generic;

import consulo.logging.Logger;
import consulo.project.Project;
import consulo.task.Task;
import consulo.task.util.TaskUtil;
import consulo.ui.ex.awt.JBScrollPane;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.AbstractCollection;
import consulo.util.xml.serializer.annotation.Property;
import consulo.util.xml.serializer.annotation.Tag;
import consulo.virtualFileSystem.fileType.FileType;
import org.jetbrains.annotations.NonNls;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Mikhail Golubev
 */
public abstract class SelectorBasedResponseHandler extends ResponseHandler {
  private static final Logger LOG = Logger.getInstance(SelectorBasedResponseHandler.class);

  // Supported selector names
  protected static final String TASKS = "tasks";

  protected static final String SUMMARY = "summary";
  protected static final String DESCRIPTION = "description";
  protected static final String ISSUE_URL = "issueUrl";
  protected static final String CLOSED = "closed";
  protected static final String UPDATED = "updated";
  protected static final String CREATED = "created";

  protected static final String SINGLE_TASK_ID = "singleTask-id";
  protected static final String SINGLE_TASK_SUMMARY = "singleTask-summary";
  protected static final String SINGLE_TASK_DESCRIPTION = "singleTask-description";
  protected static final String SINGLE_TASK_ISSUE_URL = "singleTask-issueUrl";
  protected static final String SINGLE_TASK_CLOSED = "singleTask-closed";
  protected static final String SINGLE_TASK_UPDATED = "singleTask-updated";
  protected static final String SINGLE_TASK_CREATED = "singleTask-created";
  protected static final String ID = "id";

  protected LinkedHashMap<String, Selector> mySelectors = new LinkedHashMap<String, Selector>();

  /**
   * Serialization constructor
   */
  @SuppressWarnings("UnusedDeclaration")
  protected SelectorBasedResponseHandler() {
    // empty
  }

  protected SelectorBasedResponseHandler(GenericRepository repository) {
    super(repository);
    // standard selectors
    setSelectors(ContainerUtil.newArrayList(
      // matched against list of tasks at whole downloaded from "taskListUrl"
      new Selector(TASKS),

      // matched against single tasks extracted from the list downloaded from "taskListUrl"
      new Selector(ID),
      new Selector(SUMMARY),
      new Selector(DESCRIPTION),
      new Selector(UPDATED),
      new Selector(CREATED),
      new Selector(CLOSED),
      new Selector(ISSUE_URL),

      // matched against single task downloaded from "singleTaskUrl"
      new Selector(SINGLE_TASK_ID),
      new Selector(SINGLE_TASK_SUMMARY),
      new Selector(SINGLE_TASK_DESCRIPTION),
      new Selector(SINGLE_TASK_UPDATED),
      new Selector(SINGLE_TASK_CREATED),
      new Selector(SINGLE_TASK_CLOSED),
      new Selector(SINGLE_TASK_ISSUE_URL)
    ));
  }

  @Tag("selectors")
  @Property(surroundWithTag = false)
  @AbstractCollection(surroundWithTag = false)
  @Nonnull
  public List<Selector> getSelectors() {
    return new ArrayList<Selector>(mySelectors.values());
  }

  public void setSelectors(@Nonnull List<Selector> selectors) {
    mySelectors.clear();
    for (Selector selector : selectors) {
      mySelectors.put(selector.getName(), selector);
    }
  }

  /**
   * Only predefined selectors should be accessed.
   */
  @Nonnull
  protected Selector getSelector(@Nonnull String name) {
    return mySelectors.get(name);
  }

  @Nonnull
  protected String getSelectorPath(@Nonnull String name) {
    Selector s = getSelector(name);
    return s.getPath();
  }

  @Nonnull
  @Override
  public JComponent getConfigurationComponent(@Nonnull Project project) {
    FileType fileType = getResponseType().getSelectorFileType();
    HighlightedSelectorsTable table = new HighlightedSelectorsTable(fileType, project, getSelectors());
    return new JBScrollPane(table);
  }

  @Override
  public SelectorBasedResponseHandler clone() {
    SelectorBasedResponseHandler clone = (SelectorBasedResponseHandler)super.clone();
    clone.mySelectors = new LinkedHashMap<String, Selector>(mySelectors.size());
    for (Selector selector : mySelectors.values()) {
      clone.mySelectors.put(selector.getName(), selector.clone());
    }
    return clone;
  }

  @Override
  public boolean isConfigured() {
    Selector idSelector = getSelector(ID);
    if (StringUtil.isEmpty(idSelector.getPath())) return false;
    Selector summarySelector = getSelector(SUMMARY);
    if (StringUtil.isEmpty(summarySelector.getPath())) return false;
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SelectorBasedResponseHandler)) return false;

    SelectorBasedResponseHandler handler = (SelectorBasedResponseHandler)o;

    if (!mySelectors.equals(handler.mySelectors)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return mySelectors.hashCode();
  }

  @Nonnull
  @Override
  public final Task[] parseIssues(@Nonnull String response, int max) throws Exception {
    if (StringUtil.isEmpty(getSelectorPath(TASKS)) ||
      StringUtil.isEmpty(getSelectorPath(ID)) ||
      StringUtil.isEmpty(getSelectorPath(SUMMARY))) {
      throw new Exception("Selectors 'tasks', 'id' and 'summary' are mandatory");
    }
    List<Object> tasks = selectTasksList(response, max);
    LOG.debug(String.format("Total %d tasks extracted from response", tasks.size()));
    List<Task> result = new ArrayList<Task>(tasks.size());
    for (Object context : tasks) {
      String id = selectString(getSelector(ID), context);
      String summary = selectString(getSelector(SUMMARY), context);
      assert id != null && summary != null;
      GenericTask task = new GenericTask(id, summary, myRepository);
      if (!myRepository.getDownloadTasksInSeparateRequests()) {
        String description = selectString(getSelector(DESCRIPTION), context);
        if (description != null) {
          task.setDescription(description);
        }
        String issueUrl = selectString(getSelector(ISSUE_URL), context);
        if (issueUrl != null) {
          task.setIssueUrl(issueUrl);
        }
        Boolean closed = selectBoolean(getSelector(CLOSED), context);
        if (closed != null) {
          task.setClosed(closed);
        }
        Date updated = selectDate(getSelector(UPDATED), context);
        if (updated != null) {
          task.setUpdated(updated);
        }
        Date created = selectDate(getSelector(CREATED), context);
        if (created != null) {
          task.setCreated(created);
        }
      }
      result.add(task);
    }
    return result.toArray(new Task[result.size()]);
  }

  @Nullable
  private Date selectDate(@Nonnull Selector selector, @Nonnull Object context) throws Exception {
    String s = selectString(selector, context);
    if (s == null) {
      return null;
    }
    return TaskUtil.parseDate(s);
  }

  @Nullable
  protected Boolean selectBoolean(@Nonnull Selector selector, @Nonnull Object context) throws Exception {
    String s = selectString(selector, context);
    if (s == null) {
      return null;
    }
    s = s.trim().toLowerCase();
    if (s.equals("true")) {
      return true;
    }
    else if (s.equals("false")) {
      return false;
    }
    throw new Exception(
      String.format("Expression '%s' should match boolean value. Got '%s' instead", selector.getName(), s));
  }

  @Nonnull
  protected abstract List<Object> selectTasksList(@Nonnull String response, int max) throws Exception;

  @Nullable
  protected abstract String selectString(@Nonnull Selector selector, @Nonnull Object context) throws Exception;

  @Nullable
  @Override
  public final Task parseIssue(@Nonnull String response) throws Exception {
    if (StringUtil.isEmpty(getSelectorPath(SINGLE_TASK_ID)) ||
      StringUtil.isEmpty(getSelectorPath(SINGLE_TASK_SUMMARY))) {
      throw new Exception("Selectors 'singleTask-id' and 'singleTask-summary' are mandatory");
    }
    String id = selectString(getSelector(SINGLE_TASK_ID), response);
    String summary = selectString(getSelector(SINGLE_TASK_SUMMARY), response);
    assert id != null && summary != null;
    GenericTask task = new GenericTask(id, summary, myRepository);
    String description = selectString(getSelector(SINGLE_TASK_DESCRIPTION), response);
    if (description != null) {
      task.setDescription(description);
    }
    String issueUrl = selectString(getSelector(SINGLE_TASK_ISSUE_URL), response);
    if (issueUrl != null) {
      task.setIssueUrl(issueUrl);
    }
    Boolean closed = selectBoolean(getSelector(SINGLE_TASK_CLOSED), response);
    if (closed != null) {
      task.setClosed(closed);
    }
    Date updated = selectDate(getSelector(SINGLE_TASK_UPDATED), response);
    if (updated != null) {
      task.setUpdated(updated);
    }
    Date created = selectDate(getSelector(SINGLE_TASK_CREATED), response);
    if (created != null) {
      task.setCreated(created);
    }
    return task;
  }
}
