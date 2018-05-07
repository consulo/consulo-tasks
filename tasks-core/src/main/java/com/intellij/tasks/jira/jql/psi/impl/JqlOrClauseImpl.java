package com.intellij.tasks.jira.jql.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlOrClause;
import javax.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlOrClauseImpl extends JqlBinaryClauseImpl implements JqlOrClause {
  public JqlOrClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlOrClause(this);
  }
}
