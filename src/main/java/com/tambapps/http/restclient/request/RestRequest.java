package com.tambapps.http.restclient.request;

import com.tambapps.http.restclient.request.handler.body.BodyHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.tambapps.http.restclient.methods.HttpMethods.*;

/**
 * Class that holds REST request data
 */
public class RestRequest {

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
    private final Map<String, Object> parameters = new HashMap<>();
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

    public void headers(String... args) {
      if (args.length % 2 != 0) {
        throw new IllegalArgumentException("Should have pairs of (entry, value)");
      }
      for (int i = 0; i < args.length / 2; i++) {
        header(args[i], args[i + 1]);
      }
    }

    public Builder parameter(String urlParameter, Object value) {
      this.parameters.put(urlParameter, value);
      return this;
    }

    public Builder parameters(Map<String, Object> parameters) {
      this.parameters.putAll(parameters);
      return this;
    }

    public RestRequest build() {
      return new RestRequest(endpointWithParameters(), headers, method, timeout, bodyHandler);
    }

    private String endpointWithParameters() {
      StringBuilder builder = new StringBuilder().append(endpoint);
      if (parameters.size() > 0) {
        builder.append('?');
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
          builder.append(String.format("%s=%s", entry.getKey(), entry.getValue()));
          builder.append('&');
        }
        builder.deleteCharAt(builder.length() - 1); //delete last '&'
      }
      return builder.toString();
    }
  }

}
