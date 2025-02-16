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

import com.intellij.tasks.trello.model.TrelloCard;
import com.intellij.tasks.trello.model.TrelloCommentAction;
import consulo.task.Comment;
import consulo.task.Task;
import consulo.task.TaskRepository;
import consulo.task.TaskType;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * @author Mikhail Golubev
 */
public class TrelloTask extends Task {
  private static final TrelloIconBuilder iconBuilder = new TrelloIconBuilder(16);
  private TrelloCard myCard;
  private TaskRepository myRepository;


  public TrelloTask(TrelloCard card, TaskRepository repository) {
    myCard = card;
    myRepository = repository;
  }

  @Nonnull
  @Override
  public String getId() {
    return myCard.getId();
  }

  @Nonnull
  @Override
  public String getSummary() {
    return myCard.getName();
  }

  @Nullable
  @Override
  public String getDescription() {
    return myCard.getDescription();
  }

  @Nonnull
  @Override
  public Comment[] getComments() {
    List<TrelloCommentAction> comments = myCard.getComments();
    return comments.toArray(new Comment[comments.size()]);
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return iconBuilder.buildIcon(myCard.getColors());
  }

  @Nonnull
  @Override
  public TaskType getType() {
    return TaskType.OTHER;
  }

  @Nullable
  @Override
  public Date getUpdated() {
    return null;
  }

  @Nullable
  @Override
  public Date getCreated() {
    return null;
  }

  @Override
  public boolean isClosed() {
    // IDEA-111470, IDEA-111475
    return myCard.isClosed() || !myCard.isVisible();
  }

  @Override
  public boolean isIssue() {
    return true;
  }

  @Nullable
  @Override
  public String getIssueUrl() {
    return myCard.getUrl();
  }

  @Override
  public String getPresentableName() {
    return myCard.getName();
  }

  @Nullable
  @Override
  public TaskRepository getRepository() {
    return myRepository;
  }
}
