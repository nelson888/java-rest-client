package com.tambapps.http.restclient.response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class representing headers of a REST response
 */
public class HttpHeaders {

  public static final String ACCEPT_HEADER = "Accept";
  public static final String CONTENT_TYPE_HEADER = "Content-Type";
  public static final String JSON_TYPE = "application/json";


  private final Map<String, List<String>> map;

  public HttpHeaders(Map<String, List<String>> map) {
    this.map = Collections.unmodifiableMap(map);
  }

  /**
   * Get the first value associated with the given header name
   * @param name the name of the header
   * @return the first value associated with the given header name
   */
  public String getValue(String name) {
    List<String> values = map.get(name);
    return values == null ? null : values.get(0);
  }

  /**
   * Returns whether the header with the given name has at least one value
   * @param name the  name of the header
   * @return if the header has a value
   */
  public boolean hasValue(String name) {
    List<String> values = map.get(name);
    return values != null && values.size() > 0;
  }

  /**
   * Get all the values associated with the given header name
   * @param name the name of the header
   * @return all the values associated with the given header name
   */
  public List<String> getAllValues(String name) {
    return map.get(name);
  }

  /**
   * Get all the headers and there value in form of a map
   * @return the map representing the headers
   */
  public Map<String, List<String>> getMap() {
    return map;
  }
}
