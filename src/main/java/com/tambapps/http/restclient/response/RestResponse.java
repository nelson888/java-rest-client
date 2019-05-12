package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.response.handler.ResponseHandler;

/**
 * Class representing a response from a REST service
 * @param <T> the type of the response
 */
public interface RestResponse<T> {

  int REQUEST_NOT_SENT = -1;

  int getResponseCode();

  boolean isErrorResponse();

  boolean isSuccessful();

  T getData();

  <ErrorT> ErrorT getErrorData(ResponseHandler<ErrorT> responseHandler);

  byte[] getRawErrorData();

  HttpHeaders getHeaders();

}
