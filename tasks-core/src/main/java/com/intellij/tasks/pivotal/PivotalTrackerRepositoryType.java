package com.intellij.tasks.pivotal;

import com.intellij.tasks.TasksIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.task.BaseRepositoryType;
import consulo.task.TaskRepository;
import consulo.task.TaskState;
import consulo.task.ui.TaskRepositoryEditor;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * @author Dennis.Ushakov
 */
@ExtensionImpl
public class PivotalTrackerRepositoryType extends BaseRepositoryType<PivotalTrackerRepository> {

  @Nonnull
  @Override
  public String getId() {
    return "PivotalTracker";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("PivotalTracker");
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return TasksIcons.Pivotal;
  }

  @Nonnull
  @Override
  public TaskRepository createRepository() {
    return new PivotalTrackerRepository(this);
  }

  @Override
  public Class<PivotalTrackerRepository> getRepositoryClass() {
    return PivotalTrackerRepository.class;
  }

  @Override
  public EnumSet<TaskState> getPossibleTaskStates() {
    return EnumSet.of(TaskState.SUBMITTED, TaskState.OPEN, TaskState.RESOLVED, TaskState.OTHER, TaskState.IN_PROGRESS);
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(PivotalTrackerRepository repository,
                                           Project project,
                                           Consumer<PivotalTrackerRepository> changeListener) {
    return new PivotalTrackerRepositoryEditor(project, repository, changeListener);
  }
}
