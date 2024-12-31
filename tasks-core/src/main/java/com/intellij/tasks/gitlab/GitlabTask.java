package com.intellij.tasks.gitlab;

import com.intellij.tasks.TasksIcons;
import com.intellij.tasks.gitlab.model.GitlabIssue;
import com.intellij.tasks.gitlab.model.GitlabProject;
import consulo.task.Comment;
import consulo.task.Task;
import consulo.task.TaskRepository;
import consulo.task.TaskType;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Date;

/**
 * @author Mikhail Golubev
 */
public class GitlabTask extends Task {
  private final GitlabIssue myIssue;
  private final GitlabRepository myRepository;
  private final GitlabProject myProject;

  public GitlabTask(@Nonnull GitlabRepository repository, @Nonnull GitlabIssue issue) {
    myRepository = repository;
    myIssue = issue;

    GitlabProject project = null;
    for (GitlabProject p : myRepository.getProjects()) {
      if (p.getId() == myIssue.getProjectId()) {
        project = p;
      }
    }
    myProject = project;
  }

  @Nonnull
  @Override
  public String getId() {
    // Will be in form <projectId>:<issueId>
    //return myIssue.getProjectId() + ":" + myIssue.getId();
    return String.valueOf(myIssue.getId());
  }

  @Nonnull
  @Override
  public String getPresentableId() {
    return "#" + myIssue.getLocalId();
  }

  @Nonnull
  @Override
  public String getSummary() {
    return myIssue.getTitle();
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
    return TasksIcons.Gitlab;
  }

  @Nonnull
  @Override
  public TaskType getType() {
    return TaskType.BUG;
  }

  @Nullable
  @Override
  public Date getUpdated() {
    return myIssue.getUpdatedAt();
  }

  @Nullable
  @Override
  public Date getCreated() {
    return myIssue.getCreatedAt();
  }

  @Override
  public boolean isClosed() {
    return myIssue.getState().equals("closed");
  }

  @Override
  public boolean isIssue() {
    return true;
  }

  @Nonnull
  @Override
  public String getNumber() {
    return String.valueOf(myIssue.getLocalId());
  }

  @Nullable
  @Override
  public String getProject() {
    return myProject == null ? null : myProject.getName();
  }

  @Nullable
  @Override
  public String getIssueUrl() {
    if (myProject != null) {
      return myProject.getWebUrl() + "/issues/" + myIssue.getLocalId();
    }
    return null;
  }

  @Nullable
  @Override
  public TaskRepository getRepository() {
    return myRepository;
  }
}
