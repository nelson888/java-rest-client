package com.tambapps.http.restclient.response;

abstract class AbstractRestResponse<T> implements RestResponse<T> {

  private final int responseCode;
  private final HttpHeaders headers;

  public AbstractRestResponse(int responseCode, HttpHeaders headers) {
    this.responseCode = responseCode;
    this.headers = headers;
  }

  @Override
  public HttpHeaders getHeaders() {
    return headers;
  }

  @Override
  public int getResponseCode() {
    return responseCode;
  }

}
