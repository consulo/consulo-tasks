package com.intellij.tasks.redmine;

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
 * @author Dennis.Ushakov
 */
@ExtensionImpl
public class RedmineRepositoryType extends BaseRepositoryType<RedmineRepository> {

  @Nonnull
  @Override
  public String getId() {
    return "Redmine";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("Redmine");
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return TasksIcons.Redmine;
  }

  @Nonnull
  @Override
  public TaskRepository createRepository() {
    return new RedmineRepository(this);
  }

  @Override
  public Class<RedmineRepository> getRepositoryClass() {
    return RedmineRepository.class;
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(RedmineRepository repository,
                                           Project project,
                                           Consumer<RedmineRepository> changeListener) {
    return new RedmineRepositoryEditor(project, repository, changeListener);
  }
}
