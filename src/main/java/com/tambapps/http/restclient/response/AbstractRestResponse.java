package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.util.IOUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AbstractRestResponse {

  private final int responseCode;
  private final Map<String, List<String>> headers;
  private final Exception e;
  protected final Object data;

  public AbstractRestResponse(int responseCode, Object data, Map<String, List<String>> headers) {
    this.responseCode = responseCode;
    this.headers = Collections.unmodifiableMap(headers);
    this.data = data;
    e = null;
  }

  public AbstractRestResponse(Exception e) {
    this(e, Collections.<String, List<String>>emptyMap());
  }

  public AbstractRestResponse(Exception e, Map<String, List<String>> headers) {
    this.headers = Collections.unmodifiableMap(headers);
    this.e = e;
    responseCode = -1;
    this.data = null;
  }

  public AbstractRestResponse(int responseCode, Map<String, List<String>> headers, Exception e, Object data) {
    this.responseCode = responseCode;
    this.headers = headers;
    this.e = e;
    this.data = data;
  }

  public boolean isSuccessful() {
    return e == null;
  }

  public boolean isErrorResponse() {
    return IOUtils.isErrorCode(responseCode);
  }

  public int getResponseCode() {
    return responseCode;
  }

  public Exception getException() {
    return e;
  }

  protected Object getData() {
    return data;
  }

  public Map<String, List<String>> getHeaders() {
    return headers;
  }
}
