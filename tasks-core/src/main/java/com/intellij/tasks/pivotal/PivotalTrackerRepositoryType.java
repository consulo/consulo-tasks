package com.intellij.tasks.pivotal;

import java.util.EnumSet;

import javax.annotation.Nonnull;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.TaskState;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import consulo.ui.image.Image;
import icons.TasksIcons;

/**
 * @author Dennis.Ushakov
 */
public class PivotalTrackerRepositoryType extends BaseRepositoryType<PivotalTrackerRepository> {

  @Nonnull
  @Override
  public String getName() {
    return "PivotalTracker";
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
