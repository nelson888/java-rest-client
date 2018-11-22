package com.tambapps.http.restclient.request;

import com.tambapps.http.restclient.request.handler.output.BodyHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds REST request data
 */
public class RestRequest {

  public static final String GET = "GET";
  public static final String PUT = "PUT";
  public static final String DELETE = "DELETE";
  public static final String POST = "POST";

  private final String endpoint;
  private final Map<String, String> headers;
  private final String method;
  private final Integer timeout;
  private final BodyHandler bodyHandler;

  private RestRequest(String endpoint, Map<String, String> headers, String method, Integer timeout,
      BodyHandler bodyHandler) {
    if (endpoint == null) {
      throw new IllegalArgumentException("URL cannot be null");
    }
    if (method == null) {
      throw new IllegalArgumentException("method cannot be null");
    }
    this.endpoint = endpoint;
    this.headers = Collections.unmodifiableMap(headers);
    this.method = method;
    this.timeout = timeout;
    this.bodyHandler = bodyHandler;
  }

  public static Builder builder(String endpoint) {
    return new Builder(endpoint);
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getMethod() {
    return method;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public boolean hasOutput() {
    return bodyHandler != null;
  }

  public BodyHandler getOutput() {
    return bodyHandler;
  }

  /**
   * Class allowing to build a REST request
   */
  public static class Builder {

    private final Map<String, String> headers = new HashMap<>();
    private final String endpoint;
    private String method = GET;
    private Integer timeout = null;

    private BodyHandler bodyHandler;

    public Builder(String endpoint) {
      this.endpoint = endpoint;
    }

    public Builder timeout(Integer durationInMillis) {
      timeout = durationInMillis;
      return this;
    }

    public Builder method(String method) {
      this.method = method;
      return this;
    }

    public Builder GET() {
      return method(GET);
    }

    public Builder DELETE() {
      return method(DELETE);
    }

    public Builder PUT() {
      return method(PUT);
    }

    public Builder POST() {
      return method(POST);
    }

    public Builder output(BodyHandler bodyHandler) {
      this.bodyHandler = bodyHandler;
      return this;
    }

    public Builder header(String name, String value) {
      headers.put(name, value);
      return this;
    }

    public Builder headers(Map<String, String> headers) {
      this.headers.putAll(headers);
      return this;
    }

    public RestRequest build() {
      return new RestRequest(endpoint, headers, method, timeout, bodyHandler);
    }
  }

}
