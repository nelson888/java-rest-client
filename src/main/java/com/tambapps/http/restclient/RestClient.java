package com.tambapps.http.restclient;

import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.request.handler.response.ResponseHandler;
import com.tambapps.http.restclient.request.handler.response.ResponseHandlers;
import com.tambapps.http.restclient.response.RestResponse;
import com.tambapps.http.restclient.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestClient {

  private ExecutorService executor;
  private final String baseUrl;
  private final int nbThreads ;
  private String jwt = null;

  public RestClient(String baseUrl, int nbThreads) {
    this.baseUrl = baseUrl;
    this.nbThreads = nbThreads;
  }

  public RestClient(String baseUrl) {
    this(baseUrl, 1);
  }

  private URL getUrl(String endpoint) throws MalformedURLException {
    if (baseUrl.endsWith("/")) {
      return new URL(
          endpoint.startsWith("/") ? baseUrl + endpoint.substring(1) : baseUrl + endpoint);
    } else {
      return new URL(baseUrl + (endpoint.startsWith("/") ?
          baseUrl + endpoint :
          baseUrl + '/' + endpoint));
    }
  }

  private HttpURLConnection prepareConnection(RestRequest request) throws IOException {
    HttpURLConnection connection =
        (HttpURLConnection) getUrl(request.getEndpoint()).openConnection();
    connection.setRequestMethod(request.getMethod());
    for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }
    if (jwt != null) {
      connection.setRequestProperty("Authorization", "Bearer " + jwt);
    }
    if (request.getTimeout() != null) {
      connection.setConnectTimeout(request.getTimeout());
    }
    return connection;
  }

  public RestResponse<Void, Void> execute(RestRequest request) {
    return execute(request, ResponseHandlers.noResponse());
  }

  public <T> RestResponse<T, T> execute(RestRequest request, ResponseHandler<T> responseHandler) {
    return execute(request, responseHandler, responseHandler);
  }

  public <SuccessT, ErrorT> RestResponse<SuccessT, ErrorT> execute(RestRequest request,
                                                                   ResponseHandler<SuccessT> successResponseHandler,
                                                                   ResponseHandler<ErrorT> errorResponseHandler) {
    HttpURLConnection connection;
    try {
      connection = prepareConnection(request);
      if (request.hasOutput()) {
        request.getOutput().prepareConnection(connection);
      }
    } catch (IOException e) {
      return new RestResponse<>(e);
    }

    Map<String, List<String>> responseHeaders = new HashMap<>();
    int responseCode = RestResponse.REQUEST_NOT_SENT;
    try {
      responseCode = connection.getResponseCode();
      responseHeaders.putAll(connection.getHeaderFields());
      RestResponse<SuccessT, ErrorT> response;
      boolean isErrorCode = IOUtils.isErrorCode(responseCode);
      ResponseHandler<?> responseHandler = isErrorCode ? errorResponseHandler : successResponseHandler;
      try (InputStream stream = isErrorCode ?
          connection.getErrorStream() :
          connection.getInputStream()) {
        response = new RestResponse<>(responseCode, responseHandler.convert(stream), responseHeaders);
      }
      return response;
    } catch (IOException e) {
      return new RestResponse<>(responseCode, e, responseHeaders);
    } finally {
      connection.disconnect();
    }
  }

  public void executeAsync(final RestRequest request) {
    executeAsync(request, ResponseHandlers.noResponse(), null);
  }

  public <T> void executeAsync(final RestRequest request,
                               final ResponseHandler<T> responseHandler, final Callback<T, T> callback) {
    executeAsync(request, responseHandler, responseHandler, callback);
  }
  public <SuccessT, ErrorT> void executeAsync(final RestRequest request,
                                              final ResponseHandler<SuccessT> successResponseHandler,
                                              final ResponseHandler<ErrorT> errorResponseHandler,
                                              final Callback<SuccessT, ErrorT> callback) {
    if (executor == null) {
      executor = Executors.newFixedThreadPool(nbThreads);
    }
    executor.submit(new Runnable() {
      @Override
      public void run() {
        RestResponse<SuccessT, ErrorT> response = execute(request, successResponseHandler, errorResponseHandler);
        if (callback != null) callback.call(response);
      }
    });
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }

  public void removeJwt() {
    setJwt(null);
  }

  public interface Callback<SuccessT, ErrorT> {
    void call(RestResponse<SuccessT, ErrorT> response);
  }

  public void shutDown() {
    if (executor != null) {
      executor.shutdown();
    }
  }
}
