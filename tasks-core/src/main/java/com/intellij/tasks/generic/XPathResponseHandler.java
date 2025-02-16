package com.intellij.tasks.generic;

import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Tag;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Golubev
 */
@Tag("XPathResponseHandler")
public final class XPathResponseHandler extends SelectorBasedResponseHandler {
  private final Map<String, XPath> myCompiledCache = new HashMap<String, XPath>();

  /**
   * Serialization constructor
   */
  @SuppressWarnings("UnusedDeclaration")
  public XPathResponseHandler() {
    // empty
  }

  public XPathResponseHandler(GenericRepository repository) {
    super(repository);
  }

  @Nonnull
  @Override
  protected List<Object> selectTasksList(@Nonnull String response, int max) throws Exception {
    Document document = new SAXBuilder(false).build(new StringReader(response));
    Element root = document.getRootElement();
    XPath xPath = lazyCompile(getSelector(TASKS).getPath());
    @SuppressWarnings("unchecked")
    List rawTaskElements = xPath.selectNodes(root);
    if (!rawTaskElements.isEmpty() && !(rawTaskElements.get(0) instanceof Element)) {
      throw new Exception(String.format("Expression '%s' should match list of XML elements. Got '%s' instead.",
                                        xPath.getXPath(), rawTaskElements.toString()));
    }
    return rawTaskElements.subList(0, Math.min(rawTaskElements.size(), max));
  }

  @Nullable
  @Override
  protected String selectString(@Nonnull Selector selector, @Nonnull Object context) throws Exception {
    if (StringUtil.isEmpty(selector.getPath())) {
      return null;
    }
    XPath xPath = lazyCompile(selector.getPath());
    String s = xPath.valueOf(context);
    if (s == null) {
      throw new Exception(String.format("XPath expression '%s' doesn't match", xPath.getXPath()));
    }
    return s;
  }

  @Nonnull
  private XPath lazyCompile(@Nonnull String path) throws Exception {
    XPath xPath = myCompiledCache.get(path);
    if (xPath == null) {
      try {
        xPath = XPath.newInstance(path);
        myCompiledCache.put(path, xPath);
      }
      catch (JDOMException e) {
        throw new Exception(String.format("Malformed XPath expression '%s'", path));
      }
    }
    return xPath;
  }

  @Nonnull
  @Override
  public ResponseType getResponseType() {
    return ResponseType.XML;
  }
}
