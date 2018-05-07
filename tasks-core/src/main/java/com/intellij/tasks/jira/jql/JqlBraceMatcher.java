package com.intellij.tasks.jira.jql;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

/**
 * @author Mikhail Golubev
 */
public class JqlBraceMatcher implements PairedBraceMatcher {
  private static final BracePair[] PAIRS = {new BracePair(JqlTokenTypes.LPAR, JqlTokenTypes.RPAR, false)};

  @Override
  public BracePair[] getPairs() {
    return PAIRS;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@Nonnull IElementType lbraceType, @Nullable IElementType contextType) {
    return false;
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return openingBraceOffset;
  }
}
