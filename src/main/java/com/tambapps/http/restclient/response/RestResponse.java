package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.IOUtils;

public class RestResponse<T> {

  private final int responseCode;
  private final T data;
  private final Exception e;

  public RestResponse(int responseCode, T data) {
    this.responseCode = responseCode;
    this.data = data;
    e = null;
  }

  public RestResponse(Exception e) {
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

}
