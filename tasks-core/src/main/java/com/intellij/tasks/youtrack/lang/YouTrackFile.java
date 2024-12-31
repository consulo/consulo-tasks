package com.intellij.tasks.youtrack.lang;

import jakarta.annotation.Nonnull;

import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.PsiFileBase;
import consulo.virtualFileSystem.fileType.FileType;

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
