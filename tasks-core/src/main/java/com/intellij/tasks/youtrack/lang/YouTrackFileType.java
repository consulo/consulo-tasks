package com.intellij.tasks.youtrack.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import icons.TasksIcons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
  public String getId() {
    return "YouTrack";
  }

  @Nonnull
  @Override
  public LocalizeValue getDescription() {
    return LocalizeValue.localizeTODO("YouTrack Query Language");
  }

  @Nonnull
  @Override
  public String getDefaultExtension() {
    return DEFAULT_EXTENSION;
  }

  @Nullable
  @Override
  public Image getIcon() {
    return TasksIcons.Youtrack;
  }
}
