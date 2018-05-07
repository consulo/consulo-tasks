package com.intellij.tasks.jira.jql.psi;

import javax.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public interface JqlOrderBy extends JqlElement {
  @Nonnull
  JqlSortKey[] getSortKeys();
}
