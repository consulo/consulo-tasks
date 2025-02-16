package com.intellij.tasks.jira.jql.psi.impl;

import jakarta.annotation.Nonnull;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlClause;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlNotClause;

/**
 * @author Mikhail Golubev
 */
public class JqlNotClauseImpl extends JqlElementImpl implements JqlNotClause {
  public JqlNotClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlNotClause(this);
  }

  @Override
  public JqlClause getSubClause() {
    return findChildByClass(JqlClause.class);
  }
}
