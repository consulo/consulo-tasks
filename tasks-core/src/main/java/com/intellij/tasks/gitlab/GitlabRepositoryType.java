package com.intellij.tasks.gitlab;

import javax.annotation.Nonnull;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import consulo.ui.image.Image;
import icons.TasksIcons;

/**
 * @author Mikhail Golubev
 */
public class GitlabRepositoryType extends BaseRepositoryType<GitlabRepository>{
  @Nonnull
  @Override
  public String getName() {
    return "Gitlab";
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return TasksIcons.Gitlab;
  }

  @Nonnull
  @Override
  public TaskRepository createRepository() {
    return new GitlabRepository(this);
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(GitlabRepository repository,
                                           Project project,
                                           Consumer<GitlabRepository> changeListener) {
    return new GitlabRepositoryEditor(project, repository, changeListener);
  }

  @Override
  public Class<GitlabRepository> getRepositoryClass() {
    return GitlabRepository.class;
  }
}
