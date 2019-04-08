package com.tambapps.http.restclient;

import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.response.handler.ResponseHandler;
import com.tambapps.http.restclient.response.handler.ResponseHandlers;
import com.tambapps.http.restclient.response.RestResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that sends asynchronous REST requests
 */
public class AsyncRestClient extends AbstractRestClient {

  private final ExecutorService executor;

  public AsyncRestClient(String baseUrl) {
    this(baseUrl, 1);
  }

  /**
   *
   * @param baseUrl the base url of the rest api
   * @param nbThreads the number of possible simultaneous requests
   */
  public AsyncRestClient(String baseUrl, int nbThreads) {
    this(baseUrl, Executors.newFixedThreadPool(nbThreads));
  }

  /**
   *
   * @param baseUrl the base url of the rest api
   * @param executor the executor that will execute the requests
   */
  public AsyncRestClient(String baseUrl, ExecutorService executor) {
    super(baseUrl);
    this.executor = executor;
  }

  public void execute(final RestRequest request) {
    execute(request, ResponseHandlers.noResponse(), null);
  }

  public <T> void execute(final RestRequest request,
                               final ResponseHandler<T> responseHandler, final Callback<T, Void> callback) {
    execute(request, responseHandler, ResponseHandlers.noResponse(), callback);
  }
  public <SuccessT, ErrorT> void execute(final RestRequest request,
                                              final ResponseHandler<SuccessT> successResponseHandler,
                                              final ResponseHandler<ErrorT> errorResponseHandler,
                                              final Callback<SuccessT, ErrorT> callback) {
    executor.submit(new Runnable() {
      @Override
      public void run() {
        RestResponse<SuccessT, ErrorT> response = doExecute(request, successResponseHandler, errorResponseHandler);
        if (callback != null) callback.call(response);
      }
    });
  }

  public void shutDown() {
    if (executor != null) {
      executor.shutdown();
    }
  }

  public interface Callback<SuccessT, ErrorT> {
    void call(RestResponse<SuccessT, ErrorT> response);
  }
}
