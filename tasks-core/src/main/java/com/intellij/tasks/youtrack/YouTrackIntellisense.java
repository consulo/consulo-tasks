package com.intellij.tasks.youtrack;

import consulo.colorScheme.TextAttributes;
import consulo.document.util.TextRange;
import consulo.logging.Logger;
import consulo.task.util.TaskUtil;
import consulo.util.collection.ContainerUtil;
import consulo.util.collection.Maps;
import consulo.util.dataholder.Key;
import consulo.util.lang.Pair;
import consulo.util.lang.StringUtil;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static consulo.codeEditor.DefaultLanguageHighlighterColors.*;
import static consulo.codeEditor.HighlighterColors.BAD_CHARACTER;
import static consulo.codeEditor.HighlighterColors.TEXT;

/**
 * Auxiliary class for extracting data from YouTrack intellisense responses.
 * See http://confluence.jetbrains.com/display/YTD5/Intellisense+for+issue+search for format details.
 * <p/>
 * It also provides two additional classes to represent tokens highlighting and
 * available completion items from response: {@link com.intellij.tasks.youtrack.YouTrackIntellisense.HighlightRange}
 * and {@link com.intellij.tasks.youtrack.YouTrackIntellisense.CompletionItem}.
 *
 * @author Mikhail Golubev
 */
public class YouTrackIntellisense {

  /**
   * Key used to bind YouTrackIntellisense instance to specific PsiFile
   */
  public static final Key<YouTrackIntellisense> INTELLISENSE_KEY = Key.create("youtrack.intellisense");

  private static final Logger LOG = Logger.getInstance(YouTrackIntellisense.class);

  public static final String INTELLISENSE_RESOURCE = "/rest/issue/intellisense";
  private static final Map<String, TextAttributes> TEXT_ATTRIBUTES = Map.of(
    "field", CONSTANT.getDefaultAttributes(),
    "keyword", KEYWORD.getDefaultAttributes(),
    "string", STRING.getDefaultAttributes(),
    "error", BAD_CHARACTER.getDefaultAttributes()
  );
  private static final int CACHE_SIZE = 30;

  private static final Map<Pair<String, Integer>, Response> ourCache =
    Collections.synchronizedMap(Maps.newLinkedHashMap(map -> map.size() > CACHE_SIZE));

  @Nonnull
  private static TextAttributes getAttributeByStyleClass(@Nonnull String styleClass) {
    final TextAttributes attr = TEXT_ATTRIBUTES.get(styleClass);
    return attr == null ? TEXT.getDefaultAttributes() : attr;
  }

  @Nonnull
  public List<HighlightRange> fetchHighlighting(@Nonnull String query, int caret) throws Exception {
    LOG.debug("Requesting highlighting");
    return fetch(query, caret, true).getHighlightRanges();
  }

  @Nonnull
  public List<CompletionItem> fetchCompletion(@Nonnull String query, int caret) throws Exception {
    LOG.debug("Requesting completion");
    return fetch(query, caret, false).getCompletionItems();
  }

  private final YouTrackRepository myRepository;

  public YouTrackIntellisense(@Nonnull YouTrackRepository repository) {
    myRepository = repository;
  }

  @Nonnull
  private Response fetch(@Nonnull String query, int caret, boolean ignoreCaret) throws Exception {
    LOG.debug("Query: '" + query + "' caret at: " + caret);
    final Pair<String, Integer> lookup = Pair.create(query, caret);
    Response response = null;
    if (ignoreCaret) {
      for (Pair<String, Integer> pair : ourCache.keySet()) {
        if (pair.getFirst().equals(query)) {
          response = ourCache.get(pair);
          break;
        }
      }
    }
    else {
      response = ourCache.get(lookup);
    }
    LOG.debug("Cache " + (response != null ? "hit" : "miss"));
    if (response == null) {
      final String url = String.format("%s?filter=%s&caret=%d", INTELLISENSE_RESOURCE, URLEncoder.encode(query, "utf-8"), caret);
      final long startTime = System.currentTimeMillis();
      response = new Response(myRepository.doREST(url, false).getResponseBodyAsStream());
      LOG.debug(String.format("Intellisense request to YouTrack took %d ms to complete", System.currentTimeMillis() - startTime));
      ourCache.put(lookup, response);
    }
    return response;
  }

  public YouTrackRepository getRepository() {
    return myRepository;
  }

  /**
   * Main wrapper around "IntelliSense" element in YouTrack response. It delegates further parsing
   * to {@link com.intellij.tasks.youtrack.YouTrackIntellisense.HighlightRange} and
   * {@link com.intellij.tasks.youtrack.YouTrackIntellisense.CompletionItem}
   */
  public static class Response {

    private List<HighlightRange> myHighlightRanges;
    private List<CompletionItem> myCompletionItems;

    public Response(@Nonnull InputStream stream) throws Exception {
      final Element root = new SAXBuilder().build(stream).getRootElement();
      TaskUtil.prettyFormatXmlToLog(LOG, root);
      @Nonnull final Element highlight = root.getChild("highlight");
      //assert highlight != null : "no '/IntelliSense/highlight' element in YouTrack response";
      myHighlightRanges = ContainerUtil.map(highlight.getChildren("range"), range -> new HighlightRange(range));

      @Nonnull final Element suggest = root.getChild("suggest");
      //assert suggest != null : "no '/IntelliSense/suggest' element in YouTrack response";
      myCompletionItems = ContainerUtil.map(suggest.getChildren("item"), item -> new CompletionItem(item));
    }

    @Nonnull
    public List<HighlightRange> getHighlightRanges() {
      return myHighlightRanges;
    }

    @Nonnull
    public List<CompletionItem> getCompletionItems() {
      return myCompletionItems;
    }
  }

  /**
   * Wrapper around content of "highlight/range" element of YouTrack response
   */
  public static class HighlightRange {
    private int myStart, myEnd;
    private String myStyleClass;

    public HighlightRange(@Nonnull Element rangeElement) {
      //assert "range".equals(rangeElement.getName());
      myStart = Integer.valueOf(rangeElement.getChildText("start"));
      myEnd = Integer.valueOf(rangeElement.getChildText("end"));
      myStyleClass = rangeElement.getChildText("styleClass");
    }

    public int getStart() {
      return myStart;
    }

    public int getEnd() {
      return myEnd;
    }

    @Nonnull
    public String getStyleClass() {
      return StringUtil.notNullize(myStyleClass);
    }

    @Nonnull
    public TextRange getRange() {
      return new TextRange(myStart, myEnd);
    }

    @Nonnull
    public TextRange getTextRange() {
      return TextRange.create(myStart, myEnd);
    }

    @Nonnull
    public TextAttributes getTextAttributes() {
      return getAttributeByStyleClass(myStyleClass);
    }
  }

  /**
   * Wrapper around content of "suggest/item" element in YouTrack response
   */
  public static class CompletionItem {
    private TextRange myMatchRange, myCompletionRange;
    private int myCaretPosition;
    private String myDescription;
    private String mySuffix;
    private String myPrefix;
    private String myOption;
    private String myStyleClass;

    public CompletionItem(@Nonnull Element item) {
      //assert "item".equals(item.getName())
      final Element match = item.getChild("match");
      myMatchRange = new TextRange(Integer.parseInt(match.getAttributeValue("start")),
                                   Integer.parseInt(match.getAttributeValue("end")));
      final Element completion = item.getChild("completion");
      myCompletionRange = new TextRange(Integer.parseInt(completion.getAttributeValue("start")),
                                        Integer.parseInt(completion.getAttributeValue("end")));
      myDescription = item.getChildText("description");
      myOption = item.getChildText("option");
      mySuffix = item.getChildText("suffix");
      myPrefix = item.getChildText("prefix");
      myStyleClass = item.getChildText("styleClass");
      myCaretPosition = Integer.valueOf(item.getChildText("caret"));
    }

    @Nonnull
    public TextRange getMatchRange() {
      return myMatchRange;
    }

    @Nonnull
    public TextRange getCompletionRange() {
      return myCompletionRange;
    }

    public int getCaretPosition() {
      return myCaretPosition;
    }

    @Nonnull
    public String getDescription() {
      return myDescription;
    }

    @Nonnull
    public String getSuffix() {
      return StringUtil.notNullize(mySuffix);
    }

    @Nonnull
    public String getPrefix() {
      return StringUtil.notNullize(myPrefix);
    }

    @Nonnull
    public String getOption() {
      return myOption;
    }

    @Nonnull
    public String getStyleClass() {
      return StringUtil.notNullize(myStyleClass);
    }

    @Nonnull
    TextAttributes getTextAttributes() {
      return getAttributeByStyleClass(myStyleClass);
    }
  }
}
