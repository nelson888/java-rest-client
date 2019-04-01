package com.tambapps.http.restclient.response;

import com.tambapps.http.restclient.util.IOUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class representing the body of a rest response where error type and success type can be different
 * @param <SuccessT> the success type of the response
 * @param <ErrorT> the error type of the response
 */
public class RestResponse<SuccessT, ErrorT> {

  public static final int REQUEST_NOT_SENT = -1;

  private final int responseCode;
  private final HttpHeaders headers;
  private final Exception e;
  protected final Object data;

  public RestResponse(int responseCode, Object data, Map<String, List<String>> headers) {
    this.responseCode = responseCode;
    this.headers = new HttpHeaders(headers);
    this.data = data;
    e = null;
  }

  public RestResponse(Exception e) {
    this(e, Collections.<String, List<String>>emptyMap());
  }

  public RestResponse(Exception e, Map<String, List<String>> headers) {
    this(e, REQUEST_NOT_SENT, headers);
  }

  public RestResponse(Exception e, int responseCode, Map<String, List<String>> headers) {
    this.headers = new HttpHeaders(headers);
    this.e = e;
    this.responseCode = responseCode;
    this.data = null;
  }

  public RestResponse(int responseCode, Map<String, List<String>> headers, Exception e, Object data) {
    this.responseCode = responseCode;
    this.headers = new HttpHeaders(headers);
    this.e = e;
    this.data = data;
  }

  public boolean isSuccessful() {
    return !hasException() && !isErrorResponse();
  }

  public boolean isErrorResponse() {
    return IOUtils.isErrorCode(responseCode) && responseCode != REQUEST_NOT_SENT;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public Exception getException() {
    return e;
  }

  public boolean hasException() {
    return e != null;
  }

  public <T> T getData() {
    return (T) data;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public SuccessT getSuccessData() {
    return isErrorResponse() ? null : (SuccessT) data;
  }

  public ErrorT getErrorData() {
    return isErrorResponse() ?  (ErrorT) data : null;
  }

}
