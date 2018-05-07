package com.intellij.tasks.jira.jql.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlEmptyValue;
import javax.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlEmptyValueImpl extends JqlElementImpl implements JqlEmptyValue {
  public JqlEmptyValueImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitEmptyValue(this);
  }
}
