package com.tambapps.http.restclient;

import com.tambapps.http.restclient.request.RestRequest;
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

  protected  <SuccessT, ErrorT> RestResponse<SuccessT, ErrorT> doExecute(RestRequest request,
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
