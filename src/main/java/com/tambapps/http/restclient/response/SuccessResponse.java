package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.response.handler.ResponseHandler;

public class SuccessResponse<T> extends AbstractRestResponse<T> {

  private final T data;

  public SuccessResponse(int responseCode, HttpHeaders headers, T data) {
    super(responseCode, headers);
    this.data = data;
  }

  @Override
  public boolean isErrorResponse() {
    return false;
  }

  @Override
  public boolean isSuccessful() {
    return true;
  }

  @Override
  public T getData() {
    return data;
  }

  @Override
  public <ErrorT> ErrorT getErrorData(ResponseHandler<ErrorT> responseHandler) {
    return null;
  }

  @Override
  public byte[] getRawErrorData() {
    return null;
  }
}
