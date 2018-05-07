package com.intellij.tasks.jira.jql.psi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
public interface JqlClauseWithHistoryPredicates extends JqlElement {
  @Nullable
  JqlOperand getAfter();

  @Nullable
  JqlOperand getBefore();

  @Nullable
  JqlOperand getOn();

  @Nullable
  JqlOperand getBy();

  @Nullable
  JqlOperand getDuring();

  @Nullable
  JqlOperand getFrom();

  @Nullable
  JqlOperand getTo();

  @Nonnull
  JqlHistoryPredicate[] getHistoryPredicates();
}
