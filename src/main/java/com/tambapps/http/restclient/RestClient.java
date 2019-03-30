package com.tambapps.http.restclient;

import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.request.handler.response.ResponseHandler;
import com.tambapps.http.restclient.request.handler.response.ResponseHandlers;
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

  public RestResponse<Void, Void> execute(RestRequest request) {
    return execute(request, ResponseHandlers.noResponse());
  }

  public <T> RestResponse<T, Void> execute(RestRequest request, ResponseHandler<T> responseHandler) {
    return execute(request, responseHandler, ResponseHandlers.noResponse());
  }

  public <SuccessT, ErrorT> RestResponse<SuccessT, ErrorT> execute(RestRequest request,
                                                                    ResponseHandler<SuccessT> successResponseHandler,
                                                                    ResponseHandler<ErrorT> errorResponseHandler) {
    return doExecute(request, successResponseHandler, errorResponseHandler);
  }

}
