package com.intellij.tasks.youtrack.lang;

import com.intellij.tasks.youtrack.YouTrackIntellisense;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.language.Language;
import consulo.language.editor.annotation.Annotation;
import consulo.language.editor.annotation.AnnotationHolder;
import consulo.language.editor.annotation.ExternalAnnotator;
import consulo.language.psi.PsiFile;
import consulo.logging.Logger;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.intellij.tasks.youtrack.YouTrackIntellisense.HighlightRange;
import static com.intellij.tasks.youtrack.lang.YouTrackHighlightingAnnotator.QueryInfo;

/**
 * @author Mikhail Golubev
 */
@ExtensionImpl
public class YouTrackHighlightingAnnotator extends ExternalAnnotator<QueryInfo, List<HighlightRange>> {
  private static Logger LOG = Logger.getInstance(YouTrackHighlightingAnnotator.class);

  @Nullable
  @Override
  public QueryInfo collectInformation(@Nonnull PsiFile file, @Nonnull Editor editor, boolean hasErrors) {
    final YouTrackIntellisense intellisense = file.getUserData(YouTrackIntellisense.INTELLISENSE_KEY);
    if (intellisense == null || !intellisense.getRepository().isConfigured()) {
      return null;
    }
    final String text = file.getText();
    final int offset = editor.getCaretModel().getOffset();
    //LOG.debug(String.format("Highlighting YouTrack query: '%s' (cursor=%d)", text, offset));
    return new QueryInfo(offset, text, intellisense);
  }

  @Nullable
  @Override
  public List<HighlightRange> doAnnotate(QueryInfo collectedInfo) {
    if (collectedInfo == null) {
      return Collections.emptyList();
    }
    final String query = collectedInfo.myText;
    final int offset = collectedInfo.myCaretOffset;
    try {
      return collectedInfo.myIntellisense.fetchHighlighting(query, offset);
    }
    catch (Exception e) {
      return Collections.emptyList();
    }
  }

  @Override
  public void apply(@Nonnull PsiFile file, List<HighlightRange> ranges, @Nonnull AnnotationHolder holder) {
    for (HighlightRange range : ranges) {
      if (range.getStyleClass().equals("error")) {
        holder.createErrorAnnotation(range.getTextRange(), null);
      }
      else {
        final Annotation infoAnnotation = holder.createInfoAnnotation(range.getTextRange(), null);
        infoAnnotation.setEnforcedTextAttributes(range.getTextAttributes());
      }
    }
  }

  @Nonnull
  @Override
  public Language getLanguage() {
    return YouTrackLanguage.INSTANCE;
  }

  public static class QueryInfo {
    private final int myCaretOffset;
    private final String myText;
    private final YouTrackIntellisense myIntellisense;

    private QueryInfo(int caretOffset, String text, YouTrackIntellisense repository) {
      myCaretOffset = caretOffset;
      myText = text;
      myIntellisense = repository;
    }
  }
}
