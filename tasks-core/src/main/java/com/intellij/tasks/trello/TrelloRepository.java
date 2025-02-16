/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.tasks.trello;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.intellij.tasks.trello.model.TrelloBoard;
import com.intellij.tasks.trello.model.TrelloCard;
import com.intellij.tasks.trello.model.TrelloList;
import com.intellij.tasks.trello.model.TrelloUser;
import consulo.logging.Logger;
import consulo.task.*;
import consulo.task.util.ResponseUtil;
import consulo.task.util.ResponseUtil.GsonMultipleObjectsDeserializer;
import consulo.task.util.ResponseUtil.GsonSingleObjectDeserializer;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.Comparing;
import consulo.util.lang.StringUtil;
import consulo.util.lang.function.Condition;
import consulo.util.xml.serializer.annotation.Tag;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.protocol.HttpContext;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Mikhail Golubev
 */
@Tag("Trello")
public final class TrelloRepository extends NewBaseRepositoryImpl {

  private static final Logger LOG = Logger.getInstance(TrelloRepository.class);
  static final TrelloBoard UNSPECIFIED_BOARD = new TrelloBoard() {
    @Nonnull
    @Override
    public String getName() {
      return "-- from all boards --";
    }
  };
  final static TrelloList UNSPECIFIED_LIST = new TrelloList() {
    @Nonnull
    @Override
    public String getName() {
      return "-- from all lists --";
    }
  };

  // User is actually needed only to check ownership of card (by its id)
  private TrelloUser myCurrentUser;
  private TrelloBoard myCurrentBoard;
  private TrelloList myCurrentList;
  /**
   * Include cards not assigned to current user
   */
  private boolean myIncludeAllCards;

  /**
   * Serialization constructor
   */
  @SuppressWarnings("UnusedDeclaration")
  public TrelloRepository() {
  }

  /**
   * Normal instantiation constructor
   */
  public TrelloRepository(TaskRepositoryType type) {
    super(type);
  }

  /**
   * Cloning constructor
   */
  public TrelloRepository(TrelloRepository other) {
    super(other);
    myCurrentUser = other.myCurrentUser;
    myCurrentBoard = other.myCurrentBoard;
    myCurrentList = other.myCurrentList;
    myIncludeAllCards = other.myIncludeAllCards;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (o.getClass() != getClass()) {
      return false;
    }
    final TrelloRepository repository = (TrelloRepository)o;
    if (!Comparing.equal(myCurrentUser, repository.myCurrentUser)) {
      return false;
    }
    if (!Comparing.equal(myCurrentBoard, repository.myCurrentBoard)) {
      return false;
    }
    if (!Comparing.equal(myCurrentList, repository.myCurrentList)) {
      return false;
    }
    return myIncludeAllCards == repository.myIncludeAllCards;
  }

  @SuppressWarnings("CloneDoesntCallSuperClone")
  @Nonnull
  @Override
  public BaseRepository clone() {
    return new TrelloRepository(this);
  }

  @Override
  public Task[] getIssues(@Nullable String query, int offset, int limit, boolean withClosed) throws Exception {
    final List<TrelloCard> cards = fetchCards(offset + limit, withClosed);
    return ContainerUtil.map2Array(cards, Task.class, (Function<TrelloCard, Task>)card -> new TrelloTask(card, TrelloRepository.this));
  }

  @Nullable
  @Override
  public Task findTask(@Nonnull String id) throws Exception {
    final TrelloCard card = fetchCardById(id);
    return card != null ? new TrelloTask(card, this) : null;
  }

  @Nullable
  public TrelloCard fetchCardById(@Nonnull String id) throws Exception {
    try {
      final URIBuilder url = new URIBuilder(getRestApiUrl("cards", id)).addParameter("actions", "commentCard").addParameter("fields",
                                                                                                                            TrelloCard.REQUIRED_FIELDS);
      return executeMethod(new HttpGet(url.build()), new GsonSingleObjectDeserializer<TrelloCard>(TrelloUtil.GSON, TrelloCard.class, true));
    }
    // Trello returns string "The requested resource was not found." or "invalid id"
    // if card can't be found, which not only cannot be deserialized, but also not valid JSON at all.
    catch (JsonParseException e) {
      return null;
    }
  }

  @Nullable
  public TrelloUser getCurrentUser() {
    return myCurrentUser;
  }

  public void setCurrentUser(TrelloUser currentUser) {
    myCurrentUser = currentUser;
  }

  @Nullable
  public TrelloBoard getCurrentBoard() {
    return myCurrentBoard;
  }

  public void setCurrentBoard(@Nullable TrelloBoard board) {
    myCurrentBoard = board != null && board.getId().equals(UNSPECIFIED_BOARD.getId()) ? UNSPECIFIED_BOARD : board;
  }

  @Nullable
  public TrelloList getCurrentList() {
    return myCurrentList;
  }

  public void setCurrentList(@Nullable TrelloList list) {
    myCurrentList = list != null && list.getId().equals(UNSPECIFIED_LIST.getId()) ? UNSPECIFIED_LIST : list;
  }

  @Nullable
  @Override
  public String extractId(@Nonnull String taskName) {
    return TrelloUtil.TRELLO_ID_PATTERN.matcher(taskName).matches() ? taskName : null;
  }

  /**
   * Request user information using supplied authorization token
   */
  @Nonnull
  public TrelloUser fetchUserByToken() throws Exception {
    try {
      final URIBuilder url = new URIBuilder(getRestApiUrl("members", "me")).addParameter("fields", TrelloUser.REQUIRED_FIELDS);
      return makeRequestAndDeserializeJsonResponse(url.build(), TrelloUser.class);
    }
    catch (Exception e) {
      LOG.warn("Error while fetching initial user info", e);
      // invalidate board and list if user can't be found
      myCurrentBoard = null;
      myCurrentList = null;
      throw e;
    }
  }

  @Nonnull
  public TrelloBoard fetchBoardById(@Nonnull String id) throws Exception {
    final URIBuilder url = new URIBuilder(getRestApiUrl("boards", id)).addParameter("fields", TrelloBoard.REQUIRED_FIELDS);
    try {
      return makeRequestAndDeserializeJsonResponse(url.build(), TrelloBoard.class);
    }
    catch (Exception e) {
      LOG.warn("Error while fetching initial board info", e);
      throw e;
    }
  }

  @Nonnull
  public TrelloList fetchListById(@Nonnull String id) throws Exception {
    final URIBuilder url = new URIBuilder(getRestApiUrl("lists", id)).addParameter("fields", TrelloList.REQUIRED_FIELDS);
    try {
      return makeRequestAndDeserializeJsonResponse(url.build(), TrelloList.class);
    }
    catch (Exception e) {
      LOG.warn("Error while fetching initial list info" + id, e);
      throw e;
    }
  }

  @Nonnull
  public List<TrelloList> fetchBoardLists() throws Exception {
    if (myCurrentBoard == null || myCurrentBoard == UNSPECIFIED_BOARD) {
      throw new IllegalStateException("Board not set");
    }
    final URIBuilder url = new URIBuilder(getRestApiUrl("boards", myCurrentBoard.getId(), "lists")).addParameter("fields",
                                                                                                                 TrelloList.REQUIRED_FIELDS);
    return makeRequestAndDeserializeJsonResponse(url.build(), TrelloUtil.LIST_OF_LISTS_TYPE);
  }

  @Nonnull
  public List<TrelloBoard> fetchUserBoards() throws Exception {
    if (myCurrentUser == null) {
      throw new IllegalStateException("User not set");
    }
    final URIBuilder url = new URIBuilder(getRestApiUrl("members", "me", "boards")).addParameter("filter", "open").addParameter("fields",
                                                                                                                                TrelloBoard.REQUIRED_FIELDS);
    return makeRequestAndDeserializeJsonResponse(url.build(), TrelloUtil.LIST_OF_BOARDS_TYPE);
  }

  @Nonnull
  public List<TrelloCard> fetchCards(int limit, boolean withClosed) throws Exception {
    boolean fromList = false;
    // choose most appropriate card provider
    String baseUrl;
    if (myCurrentList != null && myCurrentList != UNSPECIFIED_LIST) {
      baseUrl = getRestApiUrl("lists", myCurrentList.getId(), "cards");
      fromList = true;
    }
    else if (myCurrentBoard != null && myCurrentBoard != UNSPECIFIED_BOARD) {
      baseUrl = getRestApiUrl("boards", myCurrentBoard.getId(), "cards");
    }
    else if (myCurrentUser != null) {
      baseUrl = getRestApiUrl("members", "me", "cards");
    }
    else {
      throw new IllegalStateException("Not configured");
    }
    final URIBuilder fetchCardUrl = new URIBuilder(baseUrl).addParameter("fields", TrelloCard.REQUIRED_FIELDS).addParameter("limit",
                                                                                                                            String.valueOf(
                                                                                                                              limit));
    // 'visible' filter for some reason is not supported for lists
    if (withClosed || fromList) {
      fetchCardUrl.addParameter("filter", "all");
    }
    else {
      fetchCardUrl.addParameter("filter", "visible");
    }
    List<TrelloCard> cards = makeRequestAndDeserializeJsonResponse(fetchCardUrl.build(), TrelloUtil.LIST_OF_CARDS_TYPE);
    LOG.debug("Total " + cards.size() + " cards downloaded");
    if (!myIncludeAllCards) {
      cards = ContainerUtil.filter(cards, new Condition<TrelloCard>() {
        @Override
        public boolean value(TrelloCard card) {
          return card.getIdMembers().contains(myCurrentUser.getId());
        }
      });
      LOG.debug("Total " + cards.size() + " cards after filtering");
    }
    if (!cards.isEmpty()) {
      if (fromList) {
        baseUrl = getRestApiUrl("boards", cards.get(0).getIdBoard(), "cards");
      }
      // fix for IDEA-111470 and IDEA-111475
      // Select IDs of visible cards, e.d. cards that either archived explicitly, belong to archived list or closed board.
      // This information can't be extracted from single card description, because its 'closed' field
      // reflects only the card state and doesn't show state of parental list and board.
      // NOTE: According to Trello REST API "filter=visible" parameter may be used only when fetching cards for
      // particular board or user.
      final URIBuilder visibleCardsUrl = new URIBuilder(baseUrl).addParameter("filter", "visible").addParameter("fields", "none");
      final List<TrelloCard> visibleCards = makeRequestAndDeserializeJsonResponse(visibleCardsUrl.build(), TrelloUtil.LIST_OF_CARDS_TYPE);
      LOG.debug("Total " + visibleCards.size() + " visible cards");
      final Set<String> visibleCardsIDs = ContainerUtil.map2Set(visibleCards, card -> card.getId());
      for (TrelloCard card : cards) {
        card.setVisible(visibleCardsIDs.contains(card.getId()));
      }
    }
    return cards;
  }

  @Nonnull
  private <T> T executeMethod(@Nonnull HttpUriRequest method, @Nonnull ResponseHandler<T> handler) throws Exception {
    final HttpClient client = getHttpClient();
    final HttpResponse response = client.execute(method);
    final StatusLine statusLine = response.getStatusLine();
    if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
      final Header header = response.getFirstHeader("Content-Type");
      if (header != null && header.getValue().startsWith("text/plain")) {
        final String entityContent = ResponseUtil.getResponseContentAsString(response);
        throw new Exception(TaskBundle.message("failure.server.message", StringUtil.capitalize(entityContent)));
      }
      throw new Exception(TaskBundle.message("failure.http.error", statusLine.getStatusCode(), statusLine.getStatusCode()));
    }
    return handler.handleResponse(response);
  }

  @Nonnull
  private <T> List<T> makeRequestAndDeserializeJsonResponse(@Nonnull URI url, @Nonnull TypeToken<List<T>> type) throws Exception {
    return executeMethod(new HttpGet(url), new GsonMultipleObjectsDeserializer<T>(TrelloUtil.GSON, type));
  }

  @Nonnull
  private <T> T makeRequestAndDeserializeJsonResponse(@Nonnull URI url, @Nonnull Class<T> cls) throws Exception {
    return executeMethod(new HttpGet(url), new GsonSingleObjectDeserializer<T>(TrelloUtil.GSON, cls));
  }

  @Override
  public String getPresentableName() {
    String pseudoUrl = "trello.com";
    if (myCurrentBoard != null && myCurrentBoard != UNSPECIFIED_BOARD) {
      pseudoUrl += "/" + myCurrentBoard.getName();
    }
    if (myCurrentList != null && myCurrentList != UNSPECIFIED_LIST) {
      pseudoUrl += "/" + myCurrentList.getName();
    }
    return pseudoUrl;
  }

  public boolean isIncludeAllCards() {
    return myIncludeAllCards;
  }

  public void setIncludeAllCards(boolean includeAllCards) {
    myIncludeAllCards = includeAllCards;
  }

  @Nullable
  @Override
  public CancellableConnection createCancellableConnection() {
    return new HttpTestConnection(new HttpGet(getRestApiUrl("members", "me", "cards") + "?limit=1"));
  }

  /**
   * Add authorization token and developer key in any request to Trello's REST API
   */
  @Nullable
  @Override
  protected HttpRequestInterceptor createRequestInterceptor() {
    return new HttpRequestInterceptor() {
      @Override
      public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        // pass
        if (request instanceof HttpRequestWrapper) {
          final HttpRequestWrapper wrapper = (HttpRequestWrapper)request;
          try {
            wrapper.setURI(new URIBuilder(wrapper.getURI()).addParameter("token", myPassword)
                                                           .addParameter("key",
                                                                         TrelloRepositoryType.DEVELOPER_KEY)
                                                           .build());
          }
          catch (URISyntaxException e) {
            LOG.error("Illegal URL: " + wrapper.getURI(), e);
          }
        }
        else {
          LOG.error("Cannot add required authentication query parameters to request: " + request);
        }
      }
    };
  }

  @Override
  public boolean isConfigured() {
    return super.isConfigured() && StringUtil.isNotEmpty(myPassword);
  }

  @Nonnull
  @Override
  public String getRestApiPathPrefix() {
    return "/1";
  }

  @Override
  public String getUrl() {
    return "https://api.trello.com";
  }

  @Override
  protected int getFeatures() {
    return super.getFeatures() & ~NATIVE_SEARCH;
  }
}
