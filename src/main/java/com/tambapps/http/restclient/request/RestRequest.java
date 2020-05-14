package com.tambapps.http.restclient.request;

import static com.tambapps.http.restclient.request.HttpMethods.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tambapps.http.restclient.request.body.BodyProcessor;

/**
 * Class that holds REST request data
 */
public class RestRequest {
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String ACCEPT_HEADER = "Accept";
  private static final String JSON_TYPE = "application/json";

  private final String endpoint;
  private final Map<String, String> headers;
  private final String method;
  private final Integer timeout;
  private final BodyProcessor bodyProcessor;

  private RestRequest(String endpoint, Map<String, String> headers, String method, Integer timeout,
      BodyProcessor bodyProcessor) {
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
    this.bodyProcessor = bodyProcessor;
  }

  /**
   * Returns a new request builder for a given endpoint
   * @param endpoint the endpoint of the request
   * @return the request builde
   */
  public static Builder builder(String endpoint) {
    return new Builder(endpoint);
  }

  /**
   * Returns the endpoint
   * @return the endpoint
   */
  public String getEndpoint() {
    return endpoint;
  }

  /**
   * Returns the method
   * @return the method
   */
  public String getMethod() {
    return method;
  }

  /**
   * Returns the timeout
   * @return the timeout
   */
  public Integer getTimeout() {
    return timeout;
  }

  /**
   * Returns the headers
   * @return the headers
   */
  public Map<String, String> getHeaders() {
    return headers;
  }

  /**
   * Returns whether the output of the request will be handled or not
   * @return whether the output of the request will be handled or not
   */
  public boolean hasOutput() {
    return bodyProcessor != null;
  }

  public BodyProcessor getOutputProcessor() {
    return bodyProcessor;
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

    private BodyProcessor bodyProcessor;

    private Builder(String endpoint) {
      this.endpoint = endpoint == null ? "" : endpoint;
    }

    /**
     * Sets the time out of the request (null means no timeout)
     * @param durationInMillis the duration in milliseconds
     * @return this
     */
    public Builder timeout(Integer durationInMillis) {
      timeout = durationInMillis;
      return this;
    }

    /**
     * Sets the method of the request
     * @param method the method
     * @return this
     */
    public Builder method(String method) {
      this.method = method;
      return this;
    }

    /**
     * Sets the method of the request to 'GET'
     * @return this
     */
    public Builder GET() {
      return method(GET);
    }

    /**
     * Sets the method of the request to 'DELETE'
     * @return this
     */
    public Builder DELETE() {
      return method(DELETE);
    }

    /**
     * Sets the method of the request to 'PUT'
     * @return this
     */
    public Builder PUT() {
      return method(PUT);
    }

    /**
     * Sets the method of the request to 'POST'
     * @return this
     */
    public Builder POST() {
      return method(POST);
    }

    /**
     * Sets the body processor of this request
     * @param bodyProcessor the body processor
     * @return this
     */
    public Builder body(BodyProcessor bodyProcessor) {
      this.bodyProcessor = bodyProcessor;
      return this;
    }

    /**
     * Sets a header for this request
     * @param name the name of the header
     * @param value the value of the header
     * @return this
     */
    public Builder header(String name, String value) {
      headers.put(name, value);
      return this;
    }

    /**
     * Sets the 'Content-Type' header to json
     * @return this
     */
    public Builder jsonBody() {
      headers.put(CONTENT_TYPE_HEADER, JSON_TYPE);
      return this;
    }

    /**
     * Sets the 'Accept' header to json
     * @return this
     */
    public Builder acceptJson() {
      headers.put(ACCEPT_HEADER, JSON_TYPE);
      return this;
    }

    /**
     * Sets the 'Accept' and 'Content-Type' header to json
     * @return this
     */
    public Builder json() {
      jsonBody();
      acceptJson();
      return this;
    }

    /**
     * Adds the given headers to the request
     * @param headers the headers
     * @return this
     */
    public Builder headers(Map<String, String> headers) {
      this.headers.putAll(headers);
      return this;
    }

    /**
     * Add the pairs (name, value) as headers to this request
     * @param args the pairs (name, value)
     */
    public void headers(String... args) {
      if (args.length % 2 != 0) {
        throw new IllegalArgumentException("Should have pairs of (entry, value)");
      }
      for (int i = 0; i < args.length / 2; i++) {
        header(args[i], args[i + 1]);
      }
    }

    /**
     * Adds an url parameter to this request
     * @param urlParameter the url parameter
     * @param value the value of this parameter
     * @return this
     */
    public Builder parameter(String urlParameter, Object value) {
      this.parameters.put(urlParameter, value);
      return this;
    }

    /**
     * Adds url parameters to this request
     * @param parameters the url parameters an their values
     * @return this
     */
    public Builder parameters(Map<String, Object> parameters) {
      this.parameters.putAll(parameters);
      return this;
    }

    /**
     * Build the rest request
     * @return the rest request
     */
    public RestRequest build() {
      return new RestRequest(endpointWithParameters(), headers, method, timeout, bodyProcessor);
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
