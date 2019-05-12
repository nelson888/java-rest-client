package com.tambapps.http.restclient;

import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.response.ErrorResponse;
import com.tambapps.http.restclient.response.HttpHeaders;
import com.tambapps.http.restclient.response.SuccessResponse;
import com.tambapps.http.restclient.response.handler.ResponseHandler;
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

/**
 * Abstract class for a rest client
 */
public class AbstractRestClient {

  private final String baseUrl;
  private String jwt = null;

  /**
   *
   * @param baseUrl the base url of the rest api
   */
  protected AbstractRestClient(String baseUrl) {
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
    if (request.getTimeout() != null) {
      connection.setConnectTimeout(request.getTimeout());
    }
    return connection;
  }

  protected  <T> RestResponse<T> doExecute(RestRequest request, ResponseHandler<T> successResponseHandler) {
    HttpURLConnection connection;
    try {
      connection = prepareConnection(request);
      if (request.hasOutput()) {
        request.getOutput().prepareConnection(connection);
      }
    } catch (IOException e) {
      return new ErrorResponse<>();
    }

    Map<String, List<String>> responseHeaders = new HashMap<>();
    int responseCode = RestResponse.REQUEST_NOT_SENT;
    try {

      responseCode = connection.getResponseCode();
      responseHeaders.putAll(connection.getHeaderFields());
      boolean isErrorCode = IOUtils.isErrorCode(responseCode);
      try (InputStream is = isErrorCode ?
          connection.getErrorStream() :
          connection.getInputStream()) {
        if (isErrorCode) {
          return  new ErrorResponse<>(responseCode, new HttpHeaders(responseHeaders), IOUtils.toBytes(is));
        } else {
          return new SuccessResponse<>(responseCode, new HttpHeaders(responseHeaders), successResponseHandler.convert(is));
        }
      }
    } catch (IOException e) {
      return new ErrorResponse<>(responseCode, new HttpHeaders(responseHeaders));
    } finally {
      connection.disconnect();
    }
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }

  public void removeJwt() {
    setJwt(null);
  }

  public String getJwt() {
    return jwt;
  }

}
