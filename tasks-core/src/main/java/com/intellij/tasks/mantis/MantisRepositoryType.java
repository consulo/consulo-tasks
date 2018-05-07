package com.intellij.tasks.mantis;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import icons.TasksIcons;
import javax.annotation.Nonnull;

import javax.swing.*;

/**
 * @author Dmitry Avdeev
 */
public class MantisRepositoryType extends BaseRepositoryType<MantisRepository> {

  @Nonnull
  @Override
  public String getName() {
    return "Mantis";
  }

  @Nonnull
  @Override
  public Icon getIcon() {
    return TasksIcons.Mantis;
  }

  @Nonnull
  @Override
  public TaskRepository createRepository() {
    return new MantisRepository(this);
  }

  @Nonnull
  @Override
  public Class<MantisRepository> getRepositoryClass() {
    return MantisRepository.class;
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(final MantisRepository repository,
                                           final Project project,
                                           final Consumer<MantisRepository> changeListener) {
    return new MantisRepositoryEditor(project, repository, changeListener);
  }
}
