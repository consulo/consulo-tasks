package com.intellij.tasks.jira.jql;

import org.jetbrains.annotations.NotNull;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author Mikhail Golubev
 */
public class JqlParserDefinition implements ParserDefinition {
  private static final Logger LOG = Logger.getInstance(JqlParserDefinition.class);

  @NotNull
  @Override
  public Lexer createLexer(Project project, LanguageVersion languageVersion) {
    return new JqlLexer();
  }

  @Override
  public PsiParser createParser(Project project, LanguageVersion languageVersion) {
    return new JqlParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return JqlElementTypes.FILE;
  }

  @NotNull
  @Override
  public TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
    return JqlTokenTypes.WHITESPACES;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens(LanguageVersion languageVersion) {
    return TokenSet.EMPTY;
  }

  @NotNull
  @Override
  public TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
    return TokenSet.create(JqlTokenTypes.STRING_LITERAL);
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode node) {
    final IElementType type = node.getElementType();
    if (type instanceof JqlElementType) {
      return ((JqlElementType)type).createElement(node);
    }
    return new ASTWrapperPsiElement(node);
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider) {
    return new JqlFile(viewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }
}
