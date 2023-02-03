package com.intellij.tasks.youtrack.lang;

import consulo.language.Language;

/**
 * @author Mikhail Golubev
 */
public class YouTrackLanguage extends Language {
  public static YouTrackLanguage INSTANCE = new YouTrackLanguage();

  private YouTrackLanguage() {
    super("YouTrack");
  }

  @Override
  public boolean isCaseSensitive() {
    return false;
  }


}
