package com.intellij.tasks.jira.jql.psi;

import javax.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public interface JqlFunctionCall extends JqlOperand {
  @Nonnull
  JqlIdentifier getFunctionName();

  @Nonnull
  JqlArgumentList getArgumentList();
}
