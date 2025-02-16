package com.intellij.tasks.generic;

import consulo.task.Comment;
import consulo.task.Task;
import consulo.task.TaskRepository;
import consulo.task.TaskType;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Date;

public class GenericTask extends Task {
  private final String myId;
  private final String mySummary;
  private String myDescription;
  private Date myUpdated;
  private Date myCreated;
  private String myIssueUrl;
  private TaskRepository myRepository;
  private boolean myClosed;

  public GenericTask(final String id, final String summary, final TaskRepository repository) {
    myId = id;
    mySummary = summary;
    myRepository = repository;
  }

  @Nonnull
  @Override
  public String getId() {
    return myId;
  }

  @Nonnull
  @Override
  public String getSummary() {
    return mySummary;
  }

  @Nullable
  @Override
  public String getDescription() {
    return myDescription;
  }

  @Nonnull
  @Override
  public Comment[] getComments() {
    return Comment.EMPTY_ARRAY;
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return myRepository.getIcon();
  }

  @Nonnull
  @Override
  public TaskType getType() {
    return TaskType.OTHER;
  }

  @Nullable
  @Override
  public Date getUpdated() {
    return myUpdated;
  }

  @Nullable
  @Override
  public Date getCreated() {
    return myCreated;
  }

  @Override
  public boolean isClosed() {
    return myClosed;
  }

  @Override
  public boolean isIssue() {
    return true;
  }

  @Nullable
  @Override
  public String getIssueUrl() {
    return myIssueUrl;
  }

  @Nullable
  @Override
  public TaskRepository getRepository() {
    return myRepository;
  }

  public void setIssueUrl(@Nullable String issueUrl) {
    myIssueUrl = issueUrl;
  }

  public void setCreated(@Nullable Date created) {
    myCreated = created;
  }

  public void setUpdated(@Nullable Date updated) {
    myUpdated = updated;
  }

  public void setDescription(@Nullable String description) {
    myDescription = description;
  }

  public void setClosed(boolean closed) {
    myClosed = closed;
  }
}