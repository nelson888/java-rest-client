package com.tambapps.http.restclient.response;

/**
 * Class representing the output of a rest reseponse
 * @param <T> the type of the response
 */
public class RestResponse<T> extends AbstractRestResponse {

  public RestResponse(RestResponse2<T, T> response2) {
    super(response2.getResponseCode(), response2.getHeaders(), response2.getException(),   response2.getData());
  }

  public T getData() {
    return (T) data;
  }

}
