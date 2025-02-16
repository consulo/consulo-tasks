package com.intellij.tasks.redmine.model;

import consulo.task.util.gson.Mandatory;
import consulo.task.util.gson.RestModel;
import consulo.util.xml.serializer.annotation.Attribute;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
@RestModel
@SuppressWarnings("UnusedDeclaration")
public class RedmineProject {
  private int id;
  @Mandatory
  private String name;
  // Missing in Project information sent as part of issue
  private String identifier;
  // Available only for subprojects
  private RedmineProject parent;

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RedmineProject)) {
      return false;
    }

    RedmineProject project = (RedmineProject)o;

    return id == project.id;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Attribute("id")
  public int getId() {
    return id;
  }

  /**
   * For serialization purposes only
   */
  public void setId(int id) {
    this.id = id;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Nullable
  public String getIdentifier() {
    return identifier;
  }

  /**
   * For serialization purposes only
   */
  @Attribute("identifier")
  public void setIdentifier(@Nonnull String identifier) {
    this.identifier = identifier;
  }

  @Nullable
  public RedmineProject getParent() {
    return parent;
  }

  @Override
  public final String toString() {
    return getName();
  }
}
