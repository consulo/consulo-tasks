package com.intellij.tasks.youtrack;

import com.intellij.tasks.TasksIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.task.BaseRepositoryType;
import consulo.task.TaskState;
import consulo.task.ui.TaskRepositoryEditor;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * @author Dmitry Avdeev
 */
@ExtensionImpl
public class YouTrackRepositoryType extends BaseRepositoryType<YouTrackRepository> {

  @Nonnull
  public String getId() {
    return "YouTrack";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.localizeTODO("YouTrack");
  }

  @Nonnull
  public Image getIcon() {
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
