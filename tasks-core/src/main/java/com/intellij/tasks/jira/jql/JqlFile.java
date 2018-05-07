package com.intellij.tasks.jira.jql;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import javax.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlFile extends PsiFileBase {
  public JqlFile(@Nonnull FileViewProvider viewProvider) {
    super(viewProvider, JqlLanguage.INSTANCE);
  }

  @Nonnull
  @Override
  public FileType getFileType() {
    return JqlFileType.INSTANCE;
  }
}
