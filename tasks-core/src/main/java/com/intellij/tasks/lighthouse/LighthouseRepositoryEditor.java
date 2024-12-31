package com.intellij.tasks.lighthouse;

import consulo.project.Project;
import consulo.task.ui.BaseRepositoryEditor;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.JBLabel;

import jakarta.annotation.Nullable;
import javax.swing.*;
import java.util.function.Consumer;

/**
 * @author Dennis.Ushakov
 */
public class LighthouseRepositoryEditor extends BaseRepositoryEditor<LighthouseRepository> {
  private JTextField myProjectId;
  private JTextField myAPIKey;
  private JBLabel myProjectIDLabel;
  private JBLabel myAPIKeyLabel;

  public LighthouseRepositoryEditor(final Project project,
                                    final LighthouseRepository repository,
                                    Consumer<LighthouseRepository> changeListener) {
    super(project, repository, changeListener);
    myUserNameText.setVisible(false);
    myUsernameLabel.setVisible(false);
    myPasswordText.setVisible(false);
    myPasswordLabel.setVisible(false);

    myProjectId.setText(repository.getProjectId());
    myAPIKey.setText(repository.getAPIKey());
  }

  @Override
  public void apply() {
    myRepository.setProjectId(myProjectId.getText().trim());
    myRepository.setAPIKey(myAPIKey.getText().trim());
    super.apply();
  }

  @Nullable
  @Override
  protected JComponent createCustomPanel() {
    myProjectIDLabel = new JBLabel("Project ID:", SwingConstants.RIGHT);
    myProjectId = new JTextField();
    installListener(myProjectId);
    myAPIKeyLabel = new JBLabel("API Token:", SwingConstants.RIGHT);
    myAPIKey = new JTextField();
    installListener(myAPIKey);
    return FormBuilder.createFormBuilder().addLabeledComponent(myProjectIDLabel, myProjectId).addLabeledComponent(myAPIKeyLabel, myAPIKey)
                      .getPanel();
  }

  @Override
  public void setAnchor(@Nullable final JComponent anchor) {
    super.setAnchor(anchor);
    myProjectIDLabel.setAnchor(anchor);
    myAPIKeyLabel.setAnchor(anchor);
  }
}
