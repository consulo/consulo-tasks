package com.intellij.tasks.lighthouse;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.TaskState;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import icons.TasksIcons;
import javax.annotation.Nonnull;

import javax.swing.*;
import java.util.EnumSet;

/**
 * @author Dennis.Ushakov
 */
public class LighthouseRepositoryType extends BaseRepositoryType<LighthouseRepository> {
  @Nonnull
  @Override
  public String getName() {
    return "Lighthouse";
  }

  @Nonnull
  @Override
  public Icon getIcon() {
    return TasksIcons.Lighthouse;
  }

  @Nonnull
  @Override
  public TaskRepository createRepository() {
    return new LighthouseRepository(this);
  }

  @Override
  public Class<LighthouseRepository> getRepositoryClass() {
    return LighthouseRepository.class;
  }

  @Override
  public EnumSet<TaskState> getPossibleTaskStates() {
    return EnumSet.of(TaskState.SUBMITTED, TaskState.OPEN, TaskState.RESOLVED, TaskState.OTHER);
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(LighthouseRepository repository,
                                           Project project,
                                           Consumer<LighthouseRepository> changeListener) {
    return new LighthouseRepositoryEditor(project, repository, changeListener);
  }
}
