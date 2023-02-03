package com.intellij.tasks.mantis;

import com.intellij.tasks.TasksIcons;
import consulo.task.Comment;
import consulo.task.Task;
import consulo.task.TaskRepository;
import consulo.task.TaskType;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

public class MantisTask extends Task {
  private final String myId;
  private final String mySummary;
  private final Date myUpdated;
  private final boolean myClosed;
  private String myProjectName;
  private MantisRepository myRepository;

  public MantisTask(final String id,
                    final String summary,
                    MantisProject project,
                    MantisRepository repository,
                    final Date updated,
                    final boolean closed) {
    myId = id;
    mySummary = summary;
    myUpdated = updated;
    myClosed = closed;
    myProjectName = !MantisProject.ALL_PROJECTS.equals(project) ? project.getName() : null;
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
    return null;
  }

  @Nonnull
  @Override
  public Comment[] getComments() {
    return Comment.EMPTY_ARRAY;
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return TasksIcons.Mantis;
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
    return null;
  }

  @Override
  public boolean isClosed() {
    return myClosed;
  }

  @Override
  public boolean isIssue() {
    return true;
  }

  @Override
  public String getIssueUrl() {
    return myRepository.getUrl() + "/view.php?id=" + getId();
  }

  @Override
  public TaskRepository getRepository() {
    return myRepository;
  }

  @Nullable
  @Override
  public String getProject() {
    return myProjectName;
  }

  @Nonnull
  @Override
  public String getNumber() {
    return getId();
  }
}
