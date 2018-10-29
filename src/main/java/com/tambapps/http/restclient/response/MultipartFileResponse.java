package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.IOUtils;

import java.io.InputStream;

public class MultipartFileResponse {

  private int responseCode;
  private InputStream data;
  private Exception e;

  public MultipartFileResponse(int responseCode, InputStream data) {
    this.responseCode = responseCode;
    this.data = data;
  }

  public MultipartFileResponse(Exception e) {
    this.e = e;
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

  public InputStream getData() {
    return data;
  }
}
