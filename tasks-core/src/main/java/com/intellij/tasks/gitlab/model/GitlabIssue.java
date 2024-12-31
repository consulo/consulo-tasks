package com.intellij.tasks.gitlab.model;

import com.google.gson.annotations.SerializedName;
import jakarta.annotation.Nonnull;

import java.util.Date;

/**
 * @author Mikhail Golubev
 */
public class GitlabIssue {
  private int id;
  @SerializedName("iid")
  private int localId;
  private String title;
  private String description;
  @SerializedName("project_id")
  private int projectId;
  @SerializedName("updated_at")
  private Date updatedAt;
  @SerializedName("created_at")
  private Date createdAt;
  private String state;


  public int getId() {
    return id;
  }

  @Nonnull
  public String getTitle() {
    return title;
  }

  @Nonnull
  public String getDescription() {
    return description;
  }

  public int getProjectId() {
    return projectId;
  }

  @Nonnull
  public Date getUpdatedAt() {
    return updatedAt;
  }

  @Nonnull
  public Date getCreatedAt() {
    return createdAt;
  }

  @Nonnull
  public String getState() {
    return state;
  }

  public int getLocalId() {
    return localId;
  }
}
