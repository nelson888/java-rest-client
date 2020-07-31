package com.tambapps.http.restclient;

import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.response.HttpHeaders;
import com.tambapps.http.restclient.response.handler.ResponseHandler;
import com.tambapps.http.restclient.response.RestResponse;
import com.tambapps.http.restclient.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for a rest client
 */
public class AbstractRestClient {

  // should
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
      return new URL(endpoint.startsWith("/") ? baseUrl + endpoint.substring(1) : baseUrl + endpoint);
    } else {
      return new URL(baseUrl + (endpoint.startsWith("/") ? endpoint : '/' + endpoint));
    }
  }

  private HttpURLConnection prepareConnection(RestRequest request) throws IOException {
    HttpURLConnection connection =
        (HttpURLConnection) getUrl(request.endpoint).openConnection();
    connection.setRequestMethod(request.method);
    for (Map.Entry<String, String> header : request.headers.entrySet()) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }
    if (jwt != null) {
      connection.setRequestProperty("Authorization", "Bearer " + jwt);
    }
    if (request.timeout != null) {
      connection.setConnectTimeout(request.timeout);
    }
    return connection;
  }

  protected  <T> RestResponse<T> doExecute(RestRequest request, ResponseHandler<T> successResponseHandler) {
    HttpURLConnection connection;
    try {
      connection = prepareConnection(request);
      if (request.hasOutput()) {
        request.getOutputProcessor().prepareConnection(connection);
      }
    } catch (IOException e) {
      return new ErrorResponse<>(e.getMessage().getBytes());
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
      return new ErrorResponse<>(responseCode, new HttpHeaders(responseHeaders), e.getMessage().getBytes());
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


  private static abstract class AbstractRestResponse<T> implements RestResponse<T> {

    private final int responseCode;
    private final HttpHeaders headers;

    public AbstractRestResponse(int responseCode, HttpHeaders headers) {
      this.responseCode = responseCode;
      this.headers = headers;
    }

    @Override
    public HttpHeaders getHeaders() {
      return headers;
    }

    @Override
    public int getResponseCode() {
      return responseCode;
    }

  }

  private static class SuccessResponse<T> extends AbstractRestResponse<T> {

    private final T data;

    SuccessResponse(int responseCode, HttpHeaders headers, T data) {
      super(responseCode, headers);
      this.data = data;
    }

    @Override
    public boolean isErrorResponse() {
      return false;
    }

    @Override
    public boolean isSuccessful() {
      return true;
    }

    @Override
    public T getData() {
      return data;
    }

    @Override
    public <ErrorT> ErrorT getErrorData(ResponseHandler<ErrorT> responseHandler) {
      return null;
    }

    @Override
    public byte[] getRawErrorData() {
      return null;
    }
  }

  private static class ErrorResponse<T> extends AbstractRestResponse<T> {

    private final byte[] bytes;

    ErrorResponse(byte[] bytes) { //no response
      this(REQUEST_NOT_SENT, new HttpHeaders(Collections.<String, List<String>>emptyMap()), bytes);
    }

    ErrorResponse(int responseCode, HttpHeaders headers, byte[] bytes) {
      super(responseCode, headers);
      this.bytes = bytes;
    }

    @Override
    public boolean isErrorResponse() {
      return true;
    }

    @Override
    public boolean isSuccessful() {
      return false;
    }

    @Override
    public T getData() {
      return null;
    }

    @Override
    public <ErrorT> ErrorT getErrorData(ResponseHandler<ErrorT> responseHandler) {
      if (bytes == null) {
        return null;
      }
      try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
        return responseHandler.convert(is);
      } catch (IOException e) {
        return null;
      }
    }

    @Override
    public byte[] getRawErrorData() {
      return bytes;
    }

  }
}
