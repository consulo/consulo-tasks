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
package com.intellij.tasks.trello.model;

import com.intellij.tasks.Comment;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Date;

/**
 * @author Mikhail Golubev
 */
public class TrelloCommentAction extends Comment {
  private String id;
  private Date date;
  private TrelloUser memberCreator;
  private Data data;


  private static class Data {
    private String text = "";
    private TrelloBoard board;
    private TrelloCard card;
  }

  @Override
  public String toString() {
    return String.format("TrelloCommentAction(id=%s, text=%s)", id, data.text);
  }

  @Nonnull
  public String getId() {
    return id;
  }

  @Nonnull
  @Override
  public Date getDate() {
    return date;
  }

  @Nonnull
  @Override
  public String getText() {
    return data.text;
  }

  @Nullable
  @Override
  public String getAuthor() {
    // if user deleted it's account in web-interface his comments left
    // marked with [deleted account]
    return memberCreator == null? "[deleted account]": memberCreator.getName();
  }
}
