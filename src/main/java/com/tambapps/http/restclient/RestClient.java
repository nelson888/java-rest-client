package com.tambapps.http.restclient;

import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.response.handler.ResponseHandler;
import com.tambapps.http.restclient.response.handler.ResponseHandlers;
import com.tambapps.http.restclient.response.RestResponse;

/**
 * Class that sends synchronous REST requests
 */
public class RestClient extends AbstractRestClient {

  /**
   *
   * @param baseUrl the base url of the rest api
   */
  public RestClient(String baseUrl) {
    super(baseUrl);
  }

  public RestResponse<Void> execute(RestRequest request) {
    return execute(request, ResponseHandlers.noResponse());
  }

  public <T> RestResponse<T> execute(RestRequest request, ResponseHandler<T> responseHandler) {
    return doExecute(request, responseHandler);
  }

}
