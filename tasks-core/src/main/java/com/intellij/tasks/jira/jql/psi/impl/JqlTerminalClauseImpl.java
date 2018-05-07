package com.intellij.tasks.jira.jql.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlIdentifier;
import com.intellij.tasks.jira.jql.psi.JqlTerminalClause;

/**
 * @author Mikhail Golubev
 */
public abstract class JqlTerminalClauseImpl extends JqlElementImpl implements JqlTerminalClause {
  public JqlTerminalClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Nonnull
  @Override
  public String getFieldName() {
    return getField().getText();
  }

  @Nonnull
  @Override
  public JqlIdentifier getField() {
    JqlIdentifier identifier = findChildByClass(JqlIdentifier.class);
    assert identifier != null;
    return identifier;
  }
}
