package com.intellij.tasks.lighthouse;

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
public class LighthouseRepositoryType extends BaseRepositoryType<LighthouseRepository> {
  @Nonnull
  @Override
  public String getId() {
    return "Lighthouse";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("Lighthouse");
  }

  @Nonnull
  @Override
  public Image getIcon() {
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
