package com.tambapps.http.restclient.request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AbstractRequest {

  static final String GET = "GET";
  static final String PUT = "PUT";
  static final String DELETE = "DELETE";
  static final String POST = "POST";

  private final URL url;
  private final Map<String, String> headers;
  private final String method;
  private final Long timeout;

  public AbstractRequest(URL url, Map<String, String> headers, String method, Long timeout) {
    if (url == null) {
      throw new IllegalArgumentException("URL cannot be null");
    }
    if (method == null) {
      throw new IllegalArgumentException("method cannot be null");
    }
    this.url = url;
    this.headers = Collections.unmodifiableMap(headers);
    this.method = method;
    this.timeout = timeout;
  }

  public URL getUrl() {
    return url;
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

  protected abstract static class Builder<T extends Builder, Request extends AbstractRequest> {

    protected final Map<String, String> headers = new HashMap<>();
    protected String method = GET;
    protected Long timeout = null;
    protected URL url = null;

    public T timeout(long duration) {
      timeout = duration;
      return (T) this;
    }

    public T method(String method) {
      this.method = method;
      return (T) this;
    }

    public T GET() {
      return method(GET);
    }

    public T DELETE() {
      return method(DELETE);
    }

    public T PUT() {
      return method(PUT);
    }

    public T POST() {
      return method(POST);
    }

    public T header(String name, String value) {
      headers.put(name, value);
      return (T) this;
    }

    public T headers(Map<String, String> headers) {
      headers.putAll(headers);
      return (T) this;
    }

    public T url(String url) throws MalformedURLException {
      return url(new URL(url));
    }

    public T url(URL url) {
      this.url = url;
      return (T) this;
    }

    public abstract Request build();
  }
}
