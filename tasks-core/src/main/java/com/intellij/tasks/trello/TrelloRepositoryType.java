/*
 * Copyright 2000-2013 JetBrains s.r.o.
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

package com.intellij.tasks.trello;

import com.intellij.tasks.TasksIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.task.BaseRepositoryType;
import consulo.task.TaskRepository;
import consulo.task.ui.TaskRepositoryEditor;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author Mikhail Golubev
 */
@ExtensionImpl
public class TrelloRepositoryType extends BaseRepositoryType<TrelloRepository> {
  public static final String DEVELOPER_KEY = "d6ec3709f7141007e150de64d4701181";
  public static final String CLIENT_AUTHORIZATION_URL =
    "https://trello.com/1/authorize?key=" + DEVELOPER_KEY + "&name=JetBrains&expiration=never&response_type=token";

  @Nonnull
  @Override
  public String getId() {
    return "Trello";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("Trello");
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return TasksIcons.Trello;
  }

  @Nullable
  @Override
  public String getAdvertiser() {
    return "<html><a href=" + CLIENT_AUTHORIZATION_URL + ">Where can I get authorization token?</a></html>";
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(TrelloRepository repository, Project project, Consumer<TrelloRepository> changeListener) {
    return new TrelloRepositoryEditor(project, repository, changeListener);
  }

  @Nonnull
  @Override
  public TaskRepository createRepository() {
    return new TrelloRepository(this);
  }

  @Override
  public Class<TrelloRepository> getRepositoryClass() {
    return TrelloRepository.class;
  }
}
