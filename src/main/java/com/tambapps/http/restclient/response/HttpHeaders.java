package com.tambapps.http.restclient.response;

import java.util.List;
import java.util.Map;

/**
 * Class representing headers of a REST response
 */
public class HttpHeaders {

  private final Map<String, List<String>> map;

  public HttpHeaders(Map<String, List<String>> map) {
    this.map = map;
  }

  public String getValue(String name) {
    List<String> values = map.get(name);
    return values == null ? null : values.get(0);
  }

  public List<String> getAllValues(String name) {
    return map.get(name);
  }

  public Map<String, List<String>> getMap() {
    return map;
  }
}
