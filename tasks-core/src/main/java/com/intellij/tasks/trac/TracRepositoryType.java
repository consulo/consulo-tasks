/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.tasks.trac;

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
 * @author Dmitry Avdeev
 */
@ExtensionImpl
public class TracRepositoryType extends BaseRepositoryType<TracRepository> {

  @Nonnull
  @Override
  public String getId() {
    return "Trac";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("Trac");
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return TasksIcons.Trac;
  }

  @Nonnull
  @Override
  public TaskRepository createRepository() {
    return new TracRepository(this);
  }

  @Override
  public Class<TracRepository> getRepositoryClass() {
    return TracRepository.class;
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(TracRepository repository,
                                           Project project,
                                           Consumer<TracRepository> changeListener) {
    return new TracRepositoryEditor(project, repository, changeListener);
  }
}
