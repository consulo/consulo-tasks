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
package com.intellij.tasks.jira.jql;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.fileTypes.LanguageFileType;
import consulo.ui.image.Image;
import icons.TasksIcons;

/**
 * @author Mikhail Golubev
 */
public class JqlFileType extends LanguageFileType {
  public static final LanguageFileType INSTANCE = new JqlFileType();
  public static final String DEFAULT_EXTENSION = "jql";

  public JqlFileType() {
    super(JqlLanguage.INSTANCE);
  }

  @Nonnull
  @Override
  public String getId() {
    return "JQL";
  }

  @Nonnull
  @Override
  public String getDescription() {
    return "JIRA query language";
  }

  @Nonnull
  @Override
  public String getDefaultExtension() {
    return DEFAULT_EXTENSION;
  }

  @Nullable
  @Override
  public Image getIcon() {
    return TasksIcons.Jira;
  }
}
