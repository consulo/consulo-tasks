package com.intellij.tasks.youtrack.lang;

import javax.annotation.Nonnull;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;

/**
 * @author Mikhail Golubev
 */
public class YouTrackFile extends PsiFileBase {
  public YouTrackFile(@Nonnull FileViewProvider viewProvider) {
    super(viewProvider, YouTrackLanguage.INSTANCE);
  }

  @Nonnull
  @Override
  public FileType getFileType() {
    return YouTrackFileType.INSTANCE;
  }
}
