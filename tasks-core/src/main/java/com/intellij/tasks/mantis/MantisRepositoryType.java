package com.intellij.tasks.mantis;

import com.intellij.tasks.TasksIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.task.BaseRepositoryType;
import consulo.task.TaskRepository;
import consulo.task.ui.TaskRepositoryEditor;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author Dmitry Avdeev
 */
@ExtensionImpl
public class MantisRepositoryType extends BaseRepositoryType<MantisRepository> {

  @Nonnull
  @Override
  public String getId() {
    return "Mantis";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("Mantis");
  }

  @Nonnull
  @Override
  public Image getIcon() {
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
