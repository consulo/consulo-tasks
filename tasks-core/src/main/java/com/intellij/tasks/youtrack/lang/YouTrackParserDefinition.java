package com.intellij.tasks.youtrack.lang;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.lexer.Lexer;
import consulo.language.lexer.LexerBase;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.version.LanguageVersion;
import consulo.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
@ExtensionImpl
public class YouTrackParserDefinition implements ParserDefinition {
  private static final Logger LOG = Logger.getInstance(YouTrackParserDefinition.class);

  public static final IElementType ANY_TEXT = new IElementType("ANY_TEXT", YouTrackLanguage.INSTANCE);
  public static final IElementType QUERY = new IElementType("QUERY", YouTrackLanguage.INSTANCE);
  public static final IFileElementType FILE = new IFileElementType(YouTrackLanguage.INSTANCE);

  @Nonnull
  @Override
  public Language getLanguage() {
    return YouTrackLanguage.INSTANCE;
  }

  @Nonnull
  @Override
  public Lexer createLexer(LanguageVersion languageVersion) {
    return new YouTrackMockLexer();
  }

  @Override
  public PsiParser createParser(LanguageVersion languageVersion) {
    return new YouTrackMockParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return FILE;
  }

  @Nonnull
  @Override
  public TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
    return TokenSet.EMPTY;
  }

  @Nonnull
  @Override
  public TokenSet getCommentTokens(LanguageVersion languageVersion) {
    return TokenSet.EMPTY;
  }

  @Nonnull
  @Override
  public TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
    return TokenSet.EMPTY;
  }

  @Nonnull
  @Override
  public PsiElement createElement(ASTNode node) {
    assert node.getElementType() == QUERY;
    return new YouTrackQueryElement(node);
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider) {
    return new YouTrackFile(viewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }

  /**
   * Sole element that represents YouTrack query in PSI tree
   */
  public static class YouTrackQueryElement extends ASTWrapperPsiElement {
    YouTrackQueryElement(@Nonnull ASTNode node) {
      super(node);
    }
  }

  /**
   * Tokenize whole query as single {@code ANY_TEXT} token
   */
  private static class YouTrackMockLexer extends LexerBase {
    private int myStart;
    private int myEnd;
    private CharSequence myBuffer;

    @Override
    public void start(@Nonnull CharSequence buffer, int startOffset, int endOffset, int initialState) {
      //LOG.debug(String.format("buffer: '%s', start: %d, end: %d", buffer, startOffset, endOffset));
      myBuffer = buffer;
      myStart = startOffset;
      myEnd = endOffset;
    }

    @Override
    public int getState() {
      return 0;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
      return myStart >= myEnd ? null : ANY_TEXT;
    }

    @Override
    public int getTokenStart() {
      return myStart;
    }

    @Override
    public int getTokenEnd() {
      return myEnd;
    }

    @Override
    public void advance() {
      myStart = myEnd;
    }

    @Nonnull
    @Override
    public CharSequence getBufferSequence() {
      return myBuffer;
    }

    @Override
    public int getBufferEnd() {
      return myEnd;
    }
  }


  /**
   * Parse whole YouTrack query as single {@code QUERY} element
   */
  private static class YouTrackMockParser implements PsiParser {

    @Nonnull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder, LanguageVersion languageVersion) {
      PsiBuilder.Marker rootMarker = builder.mark();

      PsiBuilder.Marker queryMarker = builder.mark();
      assert builder.getTokenType() == null || builder.getTokenType() == ANY_TEXT;
      builder.advanceLexer();
      queryMarker.done(QUERY);
      assert builder.eof();

      rootMarker.done(root);
      return builder.getTreeBuilt();
    }
  }
}
