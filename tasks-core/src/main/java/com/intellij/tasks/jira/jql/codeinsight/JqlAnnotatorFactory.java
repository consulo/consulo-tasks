package com.intellij.tasks.jira.jql.codeinsight;

import com.intellij.tasks.jira.jql.JqlLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.AnnotatorFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 02/02/2023
 */
@ExtensionImpl
public class JqlAnnotatorFactory implements AnnotatorFactory {
  @Nullable
  @Override
  public Annotator createAnnotator() {
    return new JqlAnnotator();
  }

  @Nonnull
  @Override
  public Language getLanguage() {
    return JqlLanguage.INSTANCE;
  }
}
