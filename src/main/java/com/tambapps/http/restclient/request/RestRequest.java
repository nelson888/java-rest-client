package com.tambapps.http.restclient.request;

import com.tambapps.http.restclient.request.handler.output.RestOutputHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RestRequest {

  private static final String GET = "GET";
  private static final String PUT = "PUT";
  static final String DELETE = "DELETE";
  static final String POST = "POST";

  private final String endpoint;
  private final Map<String, String> headers;
  private final String method;
  private final Long timeout;
  private final RestOutputHandler outputHandler;

  public RestRequest(String endpoint, Map<String, String> headers, String method, Long timeout, RestOutputHandler outputHandler) {
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
    this.outputHandler = outputHandler;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getMethod() {
    return method;
  }

  public Long getTimeout() {
    return timeout;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public boolean hasOutput() {
    return outputHandler != null;
  }

  public RestOutputHandler getOutput() {
    return outputHandler;
  }

  public static Builder builder() {
    return new Builder();
  }

  protected static class Builder {

    private final Map<String, String> headers = new HashMap<>();
    private String method = GET;
    private Long timeout = null;
    private String endpoint = null;
    private RestOutputHandler outputHandler;

    public Builder timeout(long duration) {
      timeout = duration;
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

    public Builder output(RestOutputHandler outputHandler) {
      this.outputHandler = outputHandler;
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

    public Builder url(String endpoint)  {
      this.endpoint = endpoint;
      return this;
    }

    public RestRequest build() {
      return new RestRequest(endpoint, headers, method, timeout, outputHandler);
    }
  }

}
