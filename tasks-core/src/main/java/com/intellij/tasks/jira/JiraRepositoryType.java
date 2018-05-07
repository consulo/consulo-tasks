package com.intellij.tasks.jira;

import java.util.EnumSet;

import javax.annotation.Nonnull;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskState;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import consulo.ui.image.Image;
import icons.TasksIcons;

/**
 * @author Dmitry Avdeev
 */
public class JiraRepositoryType extends BaseRepositoryType<JiraRepository> {

  public JiraRepositoryType() {
  }

  @Nonnull
  public String getName() {
    return "JIRA";
  }

  @Nonnull
  public Image getIcon() {
    return TasksIcons.Jira;
  }

  @Nonnull
  public JiraRepository createRepository() {
    return new JiraRepository(this);
  }

  @Nonnull
  @Override
  public Class<JiraRepository> getRepositoryClass() {
    return JiraRepository.class;
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(JiraRepository repository,
                                           Project project,
                                           Consumer<JiraRepository> changeListener) {
    return new JiraRepositoryEditor(project, repository, changeListener);
  }

  @Override
  public EnumSet<TaskState> getPossibleTaskStates() {
    return EnumSet.of(TaskState.OPEN, TaskState.IN_PROGRESS, TaskState.REOPENED, TaskState.RESOLVED);
  }
}

