package com.intellij.tasks.jira.jql;

import javax.annotation.Nonnull;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

/**
 * @author Mikhail Golubev
 */
public class JqlElement extends ASTWrapperPsiElement {
  public JqlElement(@Nonnull ASTNode node) {
    super(node);
  }
}
