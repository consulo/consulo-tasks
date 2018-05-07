package com.intellij.tasks.jira.jql.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.tasks.jira.jql.psi.*;

import javax.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
public class JqlQueryImpl extends JqlElementImpl implements JqlQuery {
  public JqlQueryImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlQuery(this);
  }

  @Override
  public JqlClause getClause() {
    return findChildByClass(JqlClause.class);
  }

  @Nullable
  @Override
  public JqlOrderBy getOrderBy() {
    return findChildByClass(JqlOrderBy.class);
  }
}
