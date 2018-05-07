package com.intellij.tasks.jira.jql.psi;

import javax.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
public interface JqlSubClause extends JqlClause {
  @Nullable
  JqlClause getInnerClause();
}
