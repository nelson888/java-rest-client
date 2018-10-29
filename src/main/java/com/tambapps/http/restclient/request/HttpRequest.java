package com.tambapps.http.restclient.request;

import java.net.URL;
import java.util.Map;

public class HttpRequest extends AbstractRequest {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String JSON_TYPE = "application/json";

  private final String output;

  public HttpRequest(URL url, Map<String, String> headers, String method,
      Long timeout, String output) {
    super(url, headers, method, timeout);
    this.output = output;
  }

  public boolean hasOutput() {
    return output != null;
  }

  public String getOutput() {
    return output;
  }

  public static Builder builder() {
    return new Builder();
  }

  private static class Builder extends AbstractRequest.Builder<Builder, HttpRequest> {

    private String output = null;

    Builder output(String output) {
      this.output = output;
      return this;
    }

    public Builder json(String jsonData) {
      headers.put("Accept", JSON_TYPE);
      headers.put(CONTENT_TYPE, JSON_TYPE);
      return output(jsonData);
    }

    @Override
    public HttpRequest build() {
      return new HttpRequest(url, headers, method, timeout, output);
    }
  }

}
