package com.intellij.tasks.generic;

import consulo.component.util.pointer.NamedPointer;
import consulo.document.event.DocumentAdapter;
import consulo.document.event.DocumentEvent;
import consulo.language.Language;
import consulo.language.LanguagePointerUtil;
import consulo.language.editor.ui.awt.EditorTextField;
import consulo.language.editor.ui.awt.LanguageTextField;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.task.Task;
import consulo.ui.ex.awt.JBScrollPane;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler that uses legacy regex-based approach for tasks extraction.
 *
 * @author Evgeny.Zakrevsky
 * @author Mikhail Golubev
 */
@Tag("RegExResponseHandler")
public final class RegExResponseHandler extends ResponseHandler {
  private static final NamedPointer<Language> ourRegExpPointer = LanguagePointerUtil.createPointer("RegExp");

  private static final Logger LOG = Logger.getInstance(RegExResponseHandler.class);
  private static final String ID_PLACEHOLDER = "{id}";
  private static final String SUMMARY_PLACEHOLDER = "{summary}";

  private String myTaskRegex = "";

  /**
   * Serialization constructor
   */
  @SuppressWarnings("UnusedDeclaration")
  public RegExResponseHandler() {
    // empty
  }

  public RegExResponseHandler(GenericRepository repository) {
    super(repository);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RegExResponseHandler handler = (RegExResponseHandler)o;
    return myTaskRegex.equals(handler.myTaskRegex);
  }

  @Override
  public int hashCode() {
    return myTaskRegex.hashCode();
  }

  @Nonnull
  @Override
  public JComponent getConfigurationComponent(@Nonnull Project project) {
    consulo.ui.ex.awt.FormBuilder builder = consulo.ui.ex.awt.FormBuilder.createFormBuilder();
    final EditorTextField taskPatternText;

    taskPatternText = new LanguageTextField(ourRegExpPointer.get(), project, myTaskRegex, false);
    taskPatternText.addDocumentListener(new DocumentAdapter() {
      @Override
      public void documentChanged(DocumentEvent e) {
        myTaskRegex = taskPatternText.getText();
      }
    });
    String tooltip = "<html>Task pattern should be a regexp with two matching groups: ({id}.+?) and ({summary}.+?)";
    builder.addLabeledComponent("Task Pattern:", new JBScrollPane(taskPatternText)).addTooltip(tooltip);
    return builder.getPanel();
  }

  @Nonnull
  @Override
  public Task[] parseIssues(@Nonnull String response, int max) throws Exception {
    final List<String> placeholders = getPlaceholders(myTaskRegex);
    if (!placeholders.contains(ID_PLACEHOLDER) || !placeholders.contains(SUMMARY_PLACEHOLDER)) {
      throw new Exception("Incorrect Task Pattern");
    }

    final String taskPatternWithoutPlaceholders = myTaskRegex.replaceAll("\\{.+?\\}", "");
    Matcher matcher = Pattern
      .compile(taskPatternWithoutPlaceholders,
               Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CASE | Pattern.CANON_EQ)
      .matcher(response);

    List<Task> tasks = new ArrayList<Task>();
    for (int i = 0; i < max && matcher.find(); i++) {
      String id = matcher.group(placeholders.indexOf(ID_PLACEHOLDER) + 1);
      String summary = matcher.group(placeholders.indexOf(SUMMARY_PLACEHOLDER) + 1);
      tasks.add(new GenericTask(id, summary, myRepository));
    }
    return tasks.toArray(new Task[tasks.size()]);
  }

  @Nullable
  @Override
  public Task parseIssue(@Nonnull String response) throws Exception {
    return null;
  }

  private static List<String> getPlaceholders(String value) {
    if (value == null) {
      return List.of();
    }

    List<String> vars = new ArrayList<String>();
    Matcher m = Pattern.compile("\\{(.+?)\\}").matcher(value);
    while (m.find()) {
      vars.add(m.group(0));
    }
    return vars;
  }

  public String getTaskRegex() {
    return myTaskRegex;
  }

  public void setTaskRegex(String taskRegex) {
    myTaskRegex = taskRegex;
  }

  @Override
  public boolean isConfigured() {
    return !StringUtil.isEmpty(myTaskRegex);
  }

  @Nonnull
  @Override
  public ResponseType getResponseType() {
    return ResponseType.TEXT;
  }

  @Override
  public RegExResponseHandler clone() {
    RegExResponseHandler clone = (RegExResponseHandler)super.clone();
    clone.myTaskRegex = myTaskRegex;
    return clone;
  }
}
