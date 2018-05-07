package com.intellij.tasks.jira.jql.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlAndClause;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;

/**
 * @author Mikhail Golubev
 */
public class JqlAndClauseImpl extends JqlBinaryClauseImpl implements JqlAndClause {
  public JqlAndClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlAndClause(this);
  }
}
