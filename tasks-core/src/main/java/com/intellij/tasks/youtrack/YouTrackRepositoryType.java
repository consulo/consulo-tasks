package com.intellij.tasks.youtrack;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskState;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import icons.TasksIcons;
import javax.annotation.Nonnull;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.EnumSet;

/**
 * @author Dmitry Avdeev
 */
public class YouTrackRepositoryType extends BaseRepositoryType<YouTrackRepository> {

  @Nonnull
  public String getName() {
    return "YouTrack";
  }

  @Nonnull
  public Icon getIcon() {
    return TasksIcons.Youtrack;
  }

  @Nullable
  @Override
  public String getAdvertiser() {
    return "<html>Not YouTrack customer yet? Get <a href='http://www.jetbrains.com/youtrack/download/get_youtrack.html?idea_integration'>YouTrack</a></html>";
  }

  @Nonnull
  public YouTrackRepository createRepository() {
    return new YouTrackRepository(this);
  }

  @Nonnull
  @Override
  public Class<YouTrackRepository> getRepositoryClass() {
    return YouTrackRepository.class;
  }

  @Override
  public EnumSet<TaskState> getPossibleTaskStates() {
    return EnumSet.of(TaskState.IN_PROGRESS, TaskState.RESOLVED);
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(YouTrackRepository repository, Project project, Consumer<YouTrackRepository> changeListener) {
    return new YouTrackRepositoryEditor(project, repository, changeListener);
  }
}
