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

import javax.annotation.Nonnull;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.tasks.impl.BaseRepositoryType;
import com.intellij.util.Consumer;
import consulo.ui.image.Image;
import icons.TasksIcons;

/**
 * @author Dmitry Avdeev
 */
public class TracRepositoryType extends BaseRepositoryType<TracRepository> {

  @Nonnull
  @Override
  public String getName() {
    return "Trac";
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
