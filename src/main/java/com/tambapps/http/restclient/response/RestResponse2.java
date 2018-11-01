package com.tambapps.http.restclient.response;

import java.util.List;
import java.util.Map;

/**
 * Class representing the output of a rest response where error type and success type can be different
 * @param <SuccessT> the success type of the response
 * @param <ErrorT> the error type of the response
 */
public class RestResponse2<SuccessT, ErrorT> extends AbstractRestResponse {

  public RestResponse2(int responseCode, Object data, Map<String, List<String>> headers) {
    super(responseCode, data, headers);
  }

  public RestResponse2(Exception e) {
    super(e);
  }

  public RestResponse2(Exception e, Map<String, List<String>> headers) {
    super(e, headers);
  }

  public SuccessT getSuccessData() {
    return isErrorResponse() ? null : (SuccessT) data;
  }

  public ErrorT getErrorData() {
    return isErrorResponse() ?  (ErrorT) data : null;
  }

}
