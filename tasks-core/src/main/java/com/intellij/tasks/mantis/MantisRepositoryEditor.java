package com.intellij.tasks.mantis;

import consulo.logging.Logger;
import consulo.project.Project;
import consulo.task.ui.BaseRepositoryEditor;
import consulo.ui.ex.awt.ComboBox;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.JBLabel;
import consulo.util.lang.StringUtil;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Consumer;

/**
 * User: evgeny.zakrevsky
 * Date: 9/21/12
 */
public class MantisRepositoryEditor extends BaseRepositoryEditor<MantisRepository> {
  private static final Logger LOG = Logger.getInstance(MantisRepositoryEditor.class);

  private ComboBox myProjectCombobox;
  private ComboBox myFilterCombobox;
  private JBLabel myProjectLabel;
  private JBLabel myFilterLabel;


  public MantisRepositoryEditor(final Project project, final MantisRepository repository, final Consumer<MantisRepository> changeListener) {
    super(project, repository, changeListener);
    myTestButton.setText("Login");
  }


  @Override
  public void apply() {
    if (!myRepository.getUrl().equals(StringUtil.trimEnd(myURLText.getText(), "/")) ||
      !myRepository.getUsername().equals(myUserNameText.getText()) ||
      !myRepository.getPassword().equals(myPasswordText.getText())) {
      resetComboBoxes();
    }
    else {
      final Object selectedProjectObject = myProjectCombobox.getModel().getSelectedItem();
      final Object selectedFilterObject = myFilterCombobox.getModel().getSelectedItem();
      if (selectedProjectObject != null &&
        selectedFilterObject != null &&
        selectedProjectObject instanceof MantisProject &&
        selectedFilterObject instanceof MantisFilter) {
        myRepository.setProject((MantisProject)selectedProjectObject);
        myRepository.setFilter((MantisFilter)selectedFilterObject);
      }
    }
    super.apply();
  }

  private void resetComboBoxes() {
    myProjectCombobox.setModel(new DefaultComboBoxModel(new Object[]{"Login before"}));
    myFilterCombobox.setModel(new DefaultComboBoxModel(new Object[]{"Login before"}));
    myRepository.setProject(null);
    myRepository.setFilter(null);
  }

  @Override
  protected void afterTestConnection(final boolean connectionSuccessful) {
    super.afterTestConnection(connectionSuccessful);
    if (connectionSuccessful) {
      updateProjects();
    }
  }

  @Override
  public void setAnchor(@Nullable final JComponent anchor) {
    super.setAnchor(anchor);
    myProjectLabel.setAnchor(anchor);
    myFilterLabel.setAnchor(anchor);
  }

  @Nullable
  @Override
  protected JComponent createCustomPanel() {
    myProjectLabel = new JBLabel("Project:", SwingConstants.RIGHT);
    myProjectCombobox = new ComboBox(200);
    myProjectCombobox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(final ItemEvent e) {
        updateFilters();
      }
    });
    installListener(myProjectCombobox);
    myFilterLabel = new JBLabel("Filter:", SwingConstants.RIGHT);
    myFilterCombobox = new ComboBox(200);
    installListener(myFilterCombobox);
    updateProjects();
    return FormBuilder.createFormBuilder().addLabeledComponent(myProjectLabel, myProjectCombobox)
                      .addLabeledComponent(myFilterLabel, myFilterCombobox).getPanel();
  }

  private void updateProjects() {
    try {
      myProjectCombobox.setModel(new DefaultComboBoxModel(myRepository.getProjects().toArray()));
      if (myRepository.getProject() != null) myProjectCombobox.setSelectedItem(myRepository.getProject());
      updateFilters();
    }
    catch (Exception e) {
      resetComboBoxes();
    }
  }

  private void updateFilters() {
    try {
      final Object selectedItem = myProjectCombobox.getModel().getSelectedItem();
      if (selectedItem != null && selectedItem instanceof MantisProject) {
        myFilterCombobox.setModel(new DefaultComboBoxModel(myRepository.getFilters((MantisProject)selectedItem).toArray()));
        if (myRepository.getFilter() != null) myFilterCombobox.setSelectedItem(myRepository.getFilter());
        doApply();
      }
    }
    catch (Exception e) {
      resetComboBoxes();
    }
  }
}
