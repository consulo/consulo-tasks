package com.intellij.tasks.jira.jql.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlOrderBy;
import com.intellij.tasks.jira.jql.psi.JqlSortKey;
import javax.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlOrderByImpl extends JqlElementImpl implements JqlOrderBy {
  public JqlOrderByImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlOrderBy(this);
  }

  @Nonnull
  @Override
  public JqlSortKey[] getSortKeys() {
    return findChildrenByClass(JqlSortKey.class);
  }
}
