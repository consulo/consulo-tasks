package com.intellij.tasks.youtrack.lang;

import javax.annotation.Nonnull;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

/**
 * @author Mikhail Golubev
 */
public class YouTrackFileTypeFactory extends FileTypeFactory {
  @Override
  public void createFileTypes(@Nonnull FileTypeConsumer consumer) {
    consumer.consume(YouTrackFileType.INSTANCE, YouTrackFileType.DEFAULT_EXTENSION);
  }
}
