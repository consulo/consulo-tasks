package com.intellij.tasks.jira.jql.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlChangedClause;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import javax.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlChangedClauseImpl extends JqlClauseWithHistoryPredicatesImpl implements JqlChangedClause {
  public JqlChangedClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlChangedClause(this);
  }

  @Nonnull
  @Override
  public Type getType() {
    return Type.CHANGED;
  }
}
