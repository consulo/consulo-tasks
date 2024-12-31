package com.intellij.tasks.redmine;

import com.intellij.tasks.redmine.model.RedmineProject;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import consulo.task.ui.BaseRepositoryEditor;
import consulo.task.util.TaskUiUtil;
import consulo.ui.ex.awt.*;
import consulo.util.collection.Stack;
import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Mikhail Golubev
 * @author Dennis.Ushakov
 */
public class RedmineRepositoryEditor extends BaseRepositoryEditor<RedmineRepository> {
  private ComboBox myProjectCombo;
  private JTextField myAPIKey;
  private JBLabel myProjectLabel;
  private JBLabel myAPIKeyLabel;

  public RedmineRepositoryEditor(final Project project, final RedmineRepository repository, Consumer<RedmineRepository> changeListener) {
    super(project, repository, changeListener);

    myTestButton.setEnabled(myRepository.isConfigured());
    myAPIKey.setText(repository.getAPIKey());

    installListener(myProjectCombo);
    installListener(myAPIKey);

    toggleCredentialsVisibility();

    UIUtil.invokeLaterIfNeeded(new Runnable() {
      @Override
      public void run() {
        initialize();
      }
    });
  }

  @Override
  protected void afterTestConnection(boolean connectionSuccessful) {
    if (connectionSuccessful) {
      new FetchProjectsTask().queue();
    }
    else {
      myProjectCombo.removeAllItems();
    }
  }

  private void initialize() {
    final RedmineProject currentProject = myRepository.getCurrentProject();
    if (currentProject != null && myRepository.isConfigured()) {
      new FetchProjectsTask().queue();
    }
    else {
      myProjectCombo.removeAllItems();
    }
  }

  @Override
  public void apply() {
    super.apply();
    RedmineProjectItem selected = (RedmineProjectItem)myProjectCombo.getSelectedItem();
    myRepository.setCurrentProject(selected != null ? selected.myProject : null);
    myRepository.setAPIKey(myAPIKey.getText().trim());
    myTestButton.setEnabled(myRepository.isConfigured());
    toggleCredentialsVisibility();
  }

  private void toggleCredentialsVisibility() {
    myPasswordLabel.setVisible(myRepository.isUseHttpAuthentication());
    myPasswordText.setVisible(myRepository.isUseHttpAuthentication());

    myUsernameLabel.setVisible(myRepository.isUseHttpAuthentication());
    myUserNameText.setVisible(myRepository.isUseHttpAuthentication());

    myAPIKeyLabel.setVisible(!myRepository.isUseHttpAuthentication());
    myAPIKey.setVisible(!myRepository.isUseHttpAuthentication());
  }

  @Nullable
  @Override
  protected JComponent createCustomPanel() {
    myProjectLabel = new JBLabel("Project:", SwingConstants.RIGHT);
    myProjectCombo = new ComboBox(300);
    //myProjectCombo.setRenderer(new TaskUiUtil.SimpleComboBoxRenderer("Set URL and password/token first"));
    myProjectCombo.setRenderer(new ListCellRendererWrapper<RedmineProjectItem>() {
      @Override
      public void customize(JList list, RedmineProjectItem value, int index, boolean selected, boolean hasFocus) {
        if (value == null) {
          setText("Set URL and password/token first");
        }
        else {
          if (myProjectCombo.isPopupVisible()) {
            //if (value.myLevel == 0 && value.myProject != RedmineRepository.UNSPECIFIED_PROJECT) {
            //setFont(UIUtil.getListFont().deriveFont(Font.BOLD));
            //}
            setText(StringUtil.repeat("   ", value.myLevel) + value.myProject.getName());
          }
          else {
            // Do not indent selected project
            setText(value.myProject.getName());
          }
        }
      }
    });

    myAPIKeyLabel = new JBLabel("API Token:", SwingConstants.RIGHT);
    myAPIKey = new JPasswordField();
    return FormBuilder.createFormBuilder().addLabeledComponent(myAPIKeyLabel, myAPIKey).addLabeledComponent(myProjectLabel,
                                                                                                            myProjectCombo).getPanel();
  }

  @Override
  public void setAnchor(@Nullable final JComponent anchor) {
    super.setAnchor(anchor);
    myProjectLabel.setAnchor(anchor);
    myAPIKeyLabel.setAnchor(anchor);
  }

  private static class RedmineProjectItem {
    public final RedmineProject myProject;
    public final int myLevel;

    public RedmineProjectItem(@Nonnull RedmineProject project, int level) {
      myProject = project;
      myLevel = level;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null) {
        return false;
      }

      if (o instanceof RedmineProject) {
        return myProject.equals(o);
      }
      else if (o instanceof RedmineProjectItem) {
        return myProject.equals(((RedmineProjectItem)o).myProject);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return myProject.hashCode();
    }
  }

  private class FetchProjectsTask extends TaskUiUtil.ComboBoxUpdater<RedmineProjectItem> {
    private FetchProjectsTask() {
      super(RedmineRepositoryEditor.this.myProject, "Downloading Redmine projects...", myProjectCombo);
    }

    @Override
    public RedmineProjectItem getExtraItem() {
      return new RedmineProjectItem(RedmineRepository.UNSPECIFIED_PROJECT, 0);
    }

    @Nullable
    @Override
    public RedmineProjectItem getSelectedItem() {
      RedmineProject currentProject = myRepository.getCurrentProject();
      return currentProject != null ? new RedmineProjectItem(currentProject, -1) : null;
    }

    @Nonnull
    @Override
    protected List<RedmineProjectItem> fetch(@Nonnull ProgressIndicator indicator) throws Exception {
      // Seems that Redmine always return its project hierarchy in DFS order.
      // So it's easy to find level of each project using stack of parents.
      Stack<RedmineProject> parents = new Stack<RedmineProject>();
      List<RedmineProjectItem> items = new ArrayList<RedmineProjectItem>();
      for (RedmineProject project : myRepository.fetchProjects()) {
        RedmineProject parentProject = project.getParent();
        if (parentProject == null) {
          items.add(new RedmineProjectItem(project, 0));
          parents.clear();
        }
        else {
          while (!parents.isEmpty() && !parents.peek().equals(parentProject)) {
            parents.pop();
          }
          items.add(new RedmineProjectItem(project, parents.size()));
        }
        parents.push(project);
      }
      return items;
    }
  }
}
