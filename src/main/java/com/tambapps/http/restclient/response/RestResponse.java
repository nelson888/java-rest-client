package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.IOUtils;

public class RestResponse<T> {

  private int responseCode;
  private T data;
  private Exception e;

  public RestResponse(int responseCode, T data) {
    this.responseCode = responseCode;
    this.data = data;
  }

  public RestResponse(Exception e) {
    this.e = e;
    responseCode = -1;
  }

  public boolean isSuccessful() {
    return e == null;
  }

  public boolean isErrorCode(int responseCode) {
    return IOUtils.isErrorCode(responseCode);
  }

  public int getResponseCode() {
    return responseCode;
  }

  public T getData() {
    return data;
  }

}
