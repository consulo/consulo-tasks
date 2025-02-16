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
package com.intellij.tasks.jira;

import com.intellij.tasks.jira.jql.JqlLanguage;
import consulo.language.editor.ui.awt.EditorTextField;
import consulo.language.editor.ui.awt.LanguageTextField;
import consulo.project.Project;
import consulo.task.ui.BaseRepositoryEditor;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.JBLabel;

import jakarta.annotation.Nullable;
import javax.swing.*;
import java.util.function.Consumer;

/**
 * @author Mikhail Golubev
 */
public class JiraRepositoryEditor extends BaseRepositoryEditor<JiraRepository> {
  private EditorTextField mySearchQueryField;
  private JBLabel mySearchLabel;

  public JiraRepositoryEditor(Project project, JiraRepository repository, Consumer<JiraRepository> changeListener) {
    super(project, repository, changeListener);
  }

  @Override
  public void apply() {
    myRepository.setSearchQuery(mySearchQueryField.getText());
    super.apply();
  }

  @Nullable
  @Override
  protected JComponent createCustomPanel() {
    mySearchQueryField = new LanguageTextField(JqlLanguage.INSTANCE, myProject, myRepository.getSearchQuery());
    installListener(mySearchQueryField);
    mySearchLabel = new JBLabel("Search:", SwingConstants.RIGHT);
    return FormBuilder.createFormBuilder().addLabeledComponent(mySearchLabel, mySearchQueryField).getPanel();
  }

  @Override
  public void setAnchor(@Nullable final JComponent anchor) {
    super.setAnchor(anchor);
    mySearchLabel.setAnchor(anchor);
  }
}
