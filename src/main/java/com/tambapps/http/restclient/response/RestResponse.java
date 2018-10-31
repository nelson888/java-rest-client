package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.util.IOUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RestResponse<T> {

  private final int responseCode;
  private final Map<String, List<String>> headers;
  private final T data;
  private final Exception e;

  public RestResponse(int responseCode, T data, Map<String, List<String>> headers) {
    this.responseCode = responseCode;
    this.headers = Collections.unmodifiableMap(headers);
    this.data = data;
    e = null;
  }

  public RestResponse(Exception e) {
    this(e, Collections.<String, List<String>>emptyMap());
  }

  public RestResponse(Exception e, Map<String, List<String>> headers) {
    this.headers = Collections.unmodifiableMap(headers);
    this.e = e;
    responseCode = -1;
    data = null;
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

  public T getData() {
    return data;
  }

  public Map<String, List<String>> getHeaders() {
    return headers;
  }
}
