package com.intellij.tasks.generic;

import jakarta.annotation.Nonnull;

import consulo.util.xml.serializer.annotation.Attribute;
import consulo.util.xml.serializer.annotation.Tag;

/**
 * @author Mikhail Golubev
 */

@Tag("selector")
public final class Selector {
  @Nonnull
  private String myName;
  @Nonnull
  private String myPath;


  /**
   * Serialization constructor
   */
  @SuppressWarnings({"UnusedDeclatation"})
  public Selector() {
    // empty
  }

  public Selector(@Nonnull String name) {
    this(name, "");
  }

  public Selector(@Nonnull String name, @Nonnull String path) {
    myName = name;
    myPath = path;
  }

  public Selector(Selector other) {
    myName = other.myName;
    myPath = other.myPath;
  }

  @Attribute("name")
  @Nonnull
  public String getName() {
    return myName;
  }

  @Attribute("path")
  @Nonnull
  public String getPath() {
    return myPath;
  }

  public void setName(@Nonnull String name) {
    myName = name;
  }

  public void setPath(@Nonnull String path) {
    myPath = path;
  }

  public Selector clone() {
    return new Selector(this);
  }

  @Override
  public String toString() {
    return String.format("Selector(name='%s', path='%s')", getName(), getPath());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Selector selector = (Selector)o;

    if (!myName.equals(selector.myName)) return false;
    if (!myPath.equals(selector.myPath)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = myName.hashCode();
    result = (31 * result) + (myPath.hashCode());
    return result;
  }
}
