package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.response.handler.ResponseHandler;

/**
 * Class representing a response from a REST service
 * @param <T> the type of the response
 */
public interface RestResponse<T> {

  int REQUEST_NOT_SENT = -1;

  /**
   * Returns the response code
   * @return the response code
   */
  int getResponseCode();

  /**
   * Returns whether the response has an error response code or not
   * @return  whether the response has an error response code or not
   */
  boolean isErrorResponse();

  /**
   * Returns whether the response has a successful response code
   * @return whether the response has a successful response code
   */
  boolean isSuccessful();

  /**
   * Get the data of the response, if it was successful
   * @return the data
   */
  T getData();

  /**
   * Returns the error data of the response, if the response wasn't successful
   * @param responseHandler the response handler
   * @param <ErrorT> the error type
   * @return the error data
   */
  <ErrorT> ErrorT getErrorData(ResponseHandler<ErrorT> responseHandler);

  /**
   * Returns the raw error data in form of bytes, if the response wasn't successful
   * @return the raw error data in form of bytes
   */
  byte[] getRawErrorData();

  /**
   * Returns the headers of the response
   * @return the headers
   */
  HttpHeaders getHeaders();

}
