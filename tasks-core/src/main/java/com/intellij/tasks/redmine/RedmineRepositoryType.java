package com.intellij.tasks.redmine;

import javax.annotation.Nonnull;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import consulo.ui.image.Image;
import icons.TasksIcons;

/**
 * @author Dennis.Ushakov
 */
public class RedmineRepositoryType extends BaseRepositoryType<RedmineRepository> {

  @Nonnull
  @Override
  public String getName() {
    return "Redmine";
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
