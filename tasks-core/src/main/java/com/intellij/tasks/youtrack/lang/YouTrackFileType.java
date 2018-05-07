package com.intellij.tasks.youtrack.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.TasksIcons;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javax.swing.*;

/**
 * @author Mikhail Golubev
 */
public class YouTrackFileType extends LanguageFileType {
  public static final YouTrackFileType INSTANCE = new YouTrackFileType();
  public static final String DEFAULT_EXTENSION = "youtrack";

  public YouTrackFileType() {
    super(YouTrackLanguage.INSTANCE);
  }

  @Nonnull
  @Override
  public String getName() {
    return "YouTrack";
  }

  @Nonnull
  @Override
  public String getDescription() {
    return "YouTrack Query Language";
  }

  @Nonnull
  @Override
  public String getDefaultExtension() {
    return DEFAULT_EXTENSION;
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return TasksIcons.Youtrack;
  }
}
