package com.intellij.tasks.generic;

import consulo.language.file.FileTypeManager;
import consulo.language.plain.PlainTextFileType;
import consulo.logging.Logger;
import consulo.virtualFileSystem.fileType.FileType;

import jakarta.annotation.Nonnull;
import java.util.function.Supplier;


/**
 * ResponseType contains information about what selector types used
 * to extract information from server responses with specific content-type.
 *
 * @author evgeny.zakrevsky
 * @author Mikhail Golubev
 */
public enum ResponseType {

  XML("application/xml", () -> findFileTypePlainDefault("XML"), () -> findFileTypePlainDefault("XPATH")),
  JSON("application/json", () -> findFileTypePlainDefault("JSON"), () -> PlainTextFileType.INSTANCE),
  // TODO: think about possible selector type if it needed at all (e.g. CSS selector)
  HTML("text/html", () -> findFileTypePlainDefault("HTML"), () -> PlainTextFileType.INSTANCE),
  TEXT("text/plain", () -> PlainTextFileType.INSTANCE, () -> findFileTypePlainDefault("RegExp"));

  private final String myMimeType;
  private final Supplier<FileType> myContentFileType;
  private final Supplier<FileType> mySelectorFileType;

  private static Logger LOG = Logger.getInstance(ResponseType.class);


  ResponseType(@Nonnull String s, @Nonnull Supplier<FileType> contentFileType, @Nonnull Supplier<FileType> selectorFileType) {
    myMimeType = s;
    myContentFileType = contentFileType;
    mySelectorFileType = selectorFileType;
  }

  /**
   * Unfortunately XPATH instance can't be received this way, because XPathSupportLoader
   * registers XPathFileType in FileTypeManager only in unit test and debug modes
   */
  @Nonnull
  private static FileType findFileTypePlainDefault(@Nonnull final String name) {
    FileType fileType = FileTypeManager.getInstance().findFileTypeByName(name);
    return fileType == null ? PlainTextFileType.INSTANCE : fileType;
  }

  @Nonnull
  public String getMimeType() {
    return myMimeType;
  }

  @Nonnull
  public FileType getContentFileType() {
    return myContentFileType.get();
  }

  @Nonnull
  public FileType getSelectorFileType() {
    return mySelectorFileType.get();
  }
}
