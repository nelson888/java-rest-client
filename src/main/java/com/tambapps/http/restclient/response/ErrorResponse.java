package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.response.handler.ResponseHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ErrorResponse<T> extends AbstractRestResponse<T> {

  private final byte[] bytes;

  public ErrorResponse() { //no response
    this(REQUEST_NOT_SENT, new HttpHeaders(Collections.<String, List<String>>emptyMap()));
  }

  public ErrorResponse(int responseCode, HttpHeaders headers) {
    this(responseCode, headers, null);
  }

  public ErrorResponse(int responseCode, HttpHeaders headers, byte[] bytes) {
    super(responseCode, headers);
    this.bytes = bytes;
  }

  @Override
  public boolean isErrorResponse() {
    return true;
  }

  @Override
  public boolean isSuccessful() {
    return false;
  }

  @Override
  public T getData() {
    return null;
  }

  @Override
  public <ErrorT> ErrorT getErrorData(ResponseHandler<ErrorT> responseHandler) {
    if (bytes == null) {
      return null;
    }
    try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
      return responseHandler.convert(is);
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public byte[] getRawErrorData() {
    return bytes;
  }

}
