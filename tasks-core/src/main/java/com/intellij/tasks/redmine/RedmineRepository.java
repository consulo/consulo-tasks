package com.intellij.tasks.redmine;

import com.google.gson.Gson;
import com.intellij.tasks.redmine.model.RedmineIssue;
import com.intellij.tasks.redmine.model.RedmineProject;
import consulo.task.NewBaseRepositoryImpl;
import consulo.task.Task;
import consulo.task.util.RequestFailedException;
import consulo.task.util.gson.GsonUtil;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.Comparing;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Tag;
import consulo.util.xml.serializer.annotation.Transient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.TestOnly;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.intellij.tasks.redmine.model.RedmineResponseWrapper.*;
import static consulo.task.util.ResponseUtil.GsonSingleObjectDeserializer;

/**
 * @author Mikhail Golubev
 * @author Dennis.Ushakov
 */
@Tag("Redmine")
public class RedmineRepository extends NewBaseRepositoryImpl {
  private static final Gson GSON = GsonUtil.createDefaultBuilder().create();

  private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

  public static final RedmineProject UNSPECIFIED_PROJECT = new RedmineProject() {
    @Nonnull
    @Override
    public String getName() {
      return "-- from all projects --";
    }

    @Nullable
    @Override
    public String getIdentifier() {
      return getName();
    }

    @Override
    public int getId() {
      return -1;
    }
  };

  private String myAPIKey = "";
  private RedmineProject myCurrentProject;
  private List<RedmineProject> myProjects = null;

  /**
   * Serialization constructor
   */
  @SuppressWarnings({"UnusedDeclaration"})
  public RedmineRepository() {
    // empty
  }

  /**
   * Normal instantiation constructor
   */
  public RedmineRepository(RedmineRepositoryType type) {
    super(type);
    setUseHttpAuthentication(true);
  }


  /**
   * Cloning constructor
   */
  public RedmineRepository(RedmineRepository other) {
    super(other);
    setAPIKey(other.myAPIKey);
    setCurrentProject(other.getCurrentProject());
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (!(o instanceof RedmineRepository)) {
      return false;
    }
    RedmineRepository that = (RedmineRepository)o;
    if (!Comparing.equal(getAPIKey(), that.getAPIKey())) {
      return false;
    }
    if (!Comparing.equal(getCurrentProject(), that.getCurrentProject())) {
      return false;
    }
    return true;
  }

  @Nonnull
  @Override
  public RedmineRepository clone() {
    return new RedmineRepository(this);
  }

  @Nullable
  @Override
  public CancellableConnection createCancellableConnection() {
    return new NewBaseRepositoryImpl.HttpTestConnection(new HttpGet()) {
      @Override
      protected void test() throws Exception {
        // Strangely, Redmine doesn't return 401 or 403 error codes, if client sent wrong credentials, and instead
        // merely returns empty array of issues with status code of 200. This means that we should attempt to fetch
        // something more specific than issues to test proper configuration, e.g. current user information at
        // /users/current.json. Unfortunately this endpoint may be unavailable on some old servers (see IDEA-122845)
        // and in this case we have to come back to requesting issues in this case to test anything at all.

        URIBuilder uriBuilder = new URIBuilder(getRestApiUrl("users", "current.json"));
        if (isUseApiKeyAuthentication()) {
          uriBuilder.addParameter("key", getAPIKey());
        }
        myCurrentRequest.setURI(uriBuilder.build());
        HttpClient client = getHttpClient();

        HttpResponse httpResponse = client.execute(myCurrentRequest);
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine != null && statusLine.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
          myCurrentRequest = new HttpGet(getIssuesUrl(0, 1, true));
          statusLine = client.execute(myCurrentRequest).getStatusLine();
        }
        if (statusLine != null && statusLine.getStatusCode() != HttpStatus.SC_OK) {
          throw RequestFailedException.forStatusCode(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
      }
    };
  }

  @Override
  public Task[] getIssues(@Nullable String query, int offset, int limit, boolean withClosed) throws Exception {
    List<RedmineIssue> issues = fetchIssues(query, offset, limit, withClosed);
    return ContainerUtil.map2Array(issues, RedmineTask.class, issue -> new RedmineTask(RedmineRepository.this, issue));
  }

  public List<RedmineIssue> fetchIssues(String query, int offset, int limit, boolean withClosed) throws Exception {
    ensureProjectsDiscovered();
    // Legacy API, can't find proper documentation
    //if (StringUtil.isNotEmpty(query)) {
    //  builder.addParameter("fields[]", "subject").addParameter("operators[subject]", "~").addParameter("values[subject][]", query);
    //}
    HttpClient client = getHttpClient();
    HttpGet method = new HttpGet(getIssuesUrl(offset, limit, withClosed));
    IssuesWrapper wrapper = client.execute(method, new GsonSingleObjectDeserializer<IssuesWrapper>(GSON, IssuesWrapper.class));
    return wrapper == null ? Collections.<RedmineIssue>emptyList() : wrapper.getIssues();
  }

  private URI getIssuesUrl(int offset, int limit, boolean withClosed) throws URISyntaxException {
    URIBuilder builder = new URIBuilder(getRestApiUrl("issues.json")).addParameter("offset", String.valueOf(offset))
                                                                     .addParameter("limit",
                                                                                   String.valueOf(limit))
                                                                     .addParameter("status_id", withClosed ? "*" : "open")
                                                                     .addParameter("assigned_to_id", "me");
    // If project was not chosen, all available issues still fetched. Such behavior may seems strange to user.
    if (myCurrentProject != null && myCurrentProject != UNSPECIFIED_PROJECT) {
      builder.addParameter("project_id", String.valueOf(myCurrentProject.getId()));
    }
    if (isUseApiKeyAuthentication()) {
      builder.addParameter("key", myAPIKey);
    }
    return builder.build();
  }

  public List<RedmineProject> fetchProjects() throws Exception {
    HttpClient client = getHttpClient();
    // Download projects with pagination (IDEA-125056, IDEA-125157)
    List<RedmineProject> allProjects = new ArrayList<RedmineProject>();
    int offset = 0;
    ProjectsWrapper wrapper;
    do {
      URIBuilder builder = new URIBuilder(getRestApiUrl("projects.json"));
      builder.addParameter("offset", String.valueOf(offset));
      builder.addParameter("limit", "50");
      if (isUseApiKeyAuthentication()) {
        builder.addParameter("key", myAPIKey);
      }

      HttpGet method = new HttpGet(builder.toString());
      wrapper = client.execute(method, new GsonSingleObjectDeserializer<ProjectsWrapper>(GSON, ProjectsWrapper.class));
      offset += wrapper.getProjects().size();
      allProjects.addAll(wrapper.getProjects());
    }
    while (wrapper.getTotalCount() > allProjects.size() || wrapper.getProjects().isEmpty());

    myProjects = allProjects;
    return Collections.unmodifiableList(myProjects);
  }

  @Nullable
  @Override
  public Task findTask(@Nonnull String id) throws Exception {
    ensureProjectsDiscovered();
    HttpGet method = new HttpGet(getRestApiUrl("issues", id + ".json"));
    IssueWrapper wrapper = getHttpClient().execute(method, new GsonSingleObjectDeserializer<IssueWrapper>(GSON, IssueWrapper.class, true));
    if (wrapper == null) {
      return null;
    }
    return new RedmineTask(this, wrapper.getIssue());
  }

  public String getAPIKey() {
    return myAPIKey;
  }

  public void setAPIKey(String APIKey) {
    myAPIKey = APIKey;
  }

  private boolean isUseApiKeyAuthentication() {
    return !isUseHttpAuthentication() && StringUtil.isNotEmpty(myAPIKey);
  }

  @Override
  public String getPresentableName() {
    String name = super.getPresentableName();
    if (myCurrentProject != null && myCurrentProject != UNSPECIFIED_PROJECT) {
      name += "/projects/" + StringUtil.notNullize(myCurrentProject.getIdentifier(), String.valueOf(myCurrentProject.getId()));
    }
    return name;
  }

  @Override
  public boolean isConfigured() {
    if (!super.isConfigured()) {
      return false;
    }
    if (isUseHttpAuthentication()) {
      return StringUtil.isNotEmpty(myPassword) && StringUtil.isNotEmpty(myUsername);
    }
    return StringUtil.isNotEmpty(myAPIKey);
  }

  @Nullable
  @Override
  public String extractId(@Nonnull String taskName) {
    return ID_PATTERN.matcher(taskName).matches() ? taskName : null;
  }

  @Override
  protected int getFeatures() {
    return super.getFeatures() & ~NATIVE_SEARCH | BASIC_HTTP_AUTHORIZATION;
  }

  @Nullable
  public RedmineProject getCurrentProject() {
    return myCurrentProject;
  }

  public void setCurrentProject(@Nullable RedmineProject project) {
    myCurrentProject = project != null && project.getId() == -1 ? UNSPECIFIED_PROJECT : project;
  }

  @Nonnull
  public List<RedmineProject> getProjects() {
    try {
      ensureProjectsDiscovered();
    }
    catch (Exception ignored) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(myProjects);
  }

  private void ensureProjectsDiscovered() throws Exception {
    if (myProjects == null) {
      fetchProjects();
    }
  }

  @TestOnly
  @Transient
  public void setProjects(@Nonnull List<RedmineProject> projects) {
    myProjects = projects;
  }
}
