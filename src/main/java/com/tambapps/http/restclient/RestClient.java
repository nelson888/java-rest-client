package com.tambapps.http.restclient;

import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.request.handler.response.RestResponseHandler;
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

  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final String baseUrl;
  private String jwt = null;
  public RestClient(String baseUrl) {
    this.baseUrl = baseUrl;
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
    return connection;
  }

  public <T> RestResponse<T> execute(RestRequest request, RestResponseHandler<T> responseHandler) {
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
    try {
      int responseCode = connection.getResponseCode();
      responseHeaders.putAll(connection.getHeaderFields());
      RestResponse<T> response;
      try (InputStream stream = IOUtils.isErrorCode(responseCode) ?
          connection.getErrorStream() :
          connection.getInputStream()) {
        response = new RestResponse<>(responseCode, responseHandler.convert(stream), responseHeaders);
      }
      return response;
    } catch (IOException e) {
      return new RestResponse<>(e, responseHeaders);
    } finally {
      connection.disconnect();
    }
  }

  public <T> void executeAsync(final RestRequest request,
      final RestResponseHandler<T> responseHandler, final Callback<T> callback) {
    executor.submit(new Runnable() {
      @Override
      public void run() {
        RestResponse<T> response = execute(request, responseHandler);
        callback.call(response);
      }
    });
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }

  public void removeJwt() {
    setJwt(null);
  }

  public interface Callback<T> {
    void call(RestResponse<T> response);
  }

}
