package com.intellij.tasks.generic;

import consulo.project.Project;
import consulo.task.Task;
import consulo.util.xml.serializer.annotation.Transient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * ResponseHandler subclasses represent different strategies of extracting tasks from
 * task server responses (e.g. using regular expressions, XPath, JSONPath, CSS selector etc.)
 *
 * @see XPathResponseHandler
 * @see JsonPathResponseHandler
 * @see RegExResponseHandler
 * @author Mikhail Golubev
 */
public abstract class ResponseHandler implements Cloneable {

  protected GenericRepository myRepository;

  /**
   * Serialization constructor
   */
  public ResponseHandler() {
    // empty
  }

  public ResponseHandler(@Nonnull GenericRepository repository) {
    myRepository = repository;
  }

  public void setRepository(@Nonnull GenericRepository repository) {
    myRepository = repository;
  }

  @Nonnull
  @Transient
  public GenericRepository getRepository() {
    return myRepository;
  }

  @Nonnull
  public abstract JComponent getConfigurationComponent(@Nonnull Project project);

  @Nonnull
  public abstract ResponseType getResponseType();

  @Nonnull
  public abstract Task[] parseIssues(@Nonnull String response, int max) throws Exception;

  @Nullable
  public abstract Task parseIssue(@Nonnull String response) throws Exception;

  public abstract boolean isConfigured();

  @Override
  public ResponseHandler clone() {
    try {
      return (ResponseHandler) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError("ResponseHandler#clone() should be supported");
    }
  }
}
