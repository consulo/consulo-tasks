package com.intellij.tasks.jira.jql.psi;

import javax.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public interface JqlArgumentList extends JqlElement {
  @Nonnull
  JqlLiteral[] getArguments();
}
