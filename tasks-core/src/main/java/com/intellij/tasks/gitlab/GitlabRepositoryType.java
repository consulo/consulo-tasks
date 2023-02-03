package com.intellij.tasks.gitlab;

import com.intellij.tasks.TasksIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.task.BaseRepositoryType;
import consulo.task.TaskRepository;
import consulo.task.ui.TaskRepositoryEditor;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author Mikhail Golubev
 */
@ExtensionImpl
public class GitlabRepositoryType extends BaseRepositoryType<GitlabRepository> {
  @Nonnull
  @Override
  public String getId() {
    return "Gitlab";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("Gitlab");
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
