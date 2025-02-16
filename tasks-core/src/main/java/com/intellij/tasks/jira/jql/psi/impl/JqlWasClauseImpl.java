package com.intellij.tasks.jira.jql.psi.impl;

import jakarta.annotation.Nonnull;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.JqlTokenTypes;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlOperand;
import com.intellij.tasks.jira.jql.psi.JqlWasClause;

import jakarta.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
public class JqlWasClauseImpl extends JqlClauseWithHistoryPredicatesImpl implements JqlWasClause {
  public JqlWasClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlWasClause(this);
  }

  /**
   * Operand can be missing in malformed query.
   */
  @Nullable
  @Override
  public JqlOperand getOperand() {
    return findChildByClass(JqlOperand.class);
  }

  @Nonnull
  @Override
  public Type getType() {
    boolean hasNot = getNode().findChildByType(JqlTokenTypes.NOT_KEYWORD) != null;
    boolean hasIn = getNode().findChildByType(JqlTokenTypes.IN_KEYWORD) != null;
    if (hasIn && hasNot) {
      return Type.WAS_NOT_IN;
    }
    else if (hasIn) {
      return Type.WAS_IN;
    }
    else if (hasNot) {
      return Type.WAS_NOT;
    }
    else {
      return Type.WAS;
    }
  }
}
