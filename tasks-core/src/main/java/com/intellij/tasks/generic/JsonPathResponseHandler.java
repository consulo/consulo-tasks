package com.intellij.tasks.generic;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Tag;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Mikhail Golubev
 */
@Tag("JsonResponseHandler")
public final class JsonPathResponseHandler extends SelectorBasedResponseHandler {

  private static final Map<Class<?>, String> JSON_TYPES = Map.of(
    Map.class, "JSON object",
    List.class, "JSON array",
    String.class, "JSON string",
    Integer.class, "JSON number",
    Double.class, "JSON number",
    Boolean.class, "JSON boolean"
  );

  private final Map<String, JsonPath> myCompiledCache = new HashMap<String, JsonPath>();

  /**
   * Serialization constructor
   */
  @SuppressWarnings("UnusedDeclaration")
  public JsonPathResponseHandler() {
  }

  public JsonPathResponseHandler(GenericRepository repository) {
    super(repository);
  }

  @Nullable
  private Object extractRawValue(@Nonnull Selector selector, @Nonnull String source) throws Exception {
    if (StringUtil.isEmpty(selector.getPath())) {
      return null;
    }
    JsonPath jsonPath = lazyCompile(selector.getPath());
    Object value;
    try {
      value = jsonPath.read(source);
    }
    catch (InvalidPathException e) {
      throw new Exception(String.format("JsonPath expression '%s' doesn't match", selector.getPath()), e);
    }
    if (value == null) {
      return null;
    }
    return value;
  }

  @Nullable
  private <T> T extractValueAndCheckType(@Nonnull Selector selector, @Nonnull String source, Class<T> cls) throws Exception {
    final Object value = extractRawValue(selector, source);
    if (value == null) {
      return null;
    }
    if (!(cls.isInstance(value))) {
      throw new Exception(
        String.format("JsonPath expression '%s' should match %s. Got '%s' instead",
                      selector.getPath(), JSON_TYPES.get(cls), value));
    }
    @SuppressWarnings("unchecked")
    T casted = (T)value;
    return casted;
  }

  @Nonnull
  @Override
  protected List<Object> selectTasksList(@Nonnull String response, int max) throws Exception {
    @SuppressWarnings("unchecked")
    List<Object> list = (List<Object>)extractValueAndCheckType(getSelector(TASKS), response, List.class);
    if (list == null) {
      return List.of();
    }
    return ContainerUtil.map2List(list, (Function<Object, Object>)Object::toString).subList(0, Math.min(list.size(), max));
  }

  @Nullable
  @Override
  protected String selectString(@Nonnull Selector selector, @Nonnull Object context) throws Exception {
    //return extractValueAndCheckType((String)context, selector, String.class);
    final Object value = extractRawValue(selector, (String)context);
    if (value == null) {
      return null;
    }
    if (value instanceof String || value instanceof Number || value instanceof Boolean) {
      return value.toString();
    }
    throw new Exception(String.format("JsonPath expression '%s' should match string value. Got '%s' instead",
                                      selector.getPath(), value));
  }

  @Nullable
  @Override
  protected Boolean selectBoolean(@Nonnull Selector selector, @Nonnull Object context) throws Exception {
    return extractValueAndCheckType(selector, (String)context, Boolean.class);
  }

  @SuppressWarnings("UnusedDeclaration")
  @Nullable
  private Long selectLong(@Nonnull Selector selector, @Nonnull String source) throws Exception {
    return extractValueAndCheckType(selector, source, Long.class);
  }

  @Nonnull
  private JsonPath lazyCompile(@Nonnull String path) throws Exception {
    JsonPath jsonPath = myCompiledCache.get(path);
    if (jsonPath == null) {
      try {
        jsonPath = JsonPath.compile(path);
        myCompiledCache.put(path, jsonPath);
      }
      catch (InvalidPathException e) {
        throw new Exception(String.format("Malformed JsonPath expression '%s'", path));
      }
    }
    return jsonPath;
  }

  @Nonnull
  @Override
  public ResponseType getResponseType() {
    return ResponseType.JSON;
  }
}
