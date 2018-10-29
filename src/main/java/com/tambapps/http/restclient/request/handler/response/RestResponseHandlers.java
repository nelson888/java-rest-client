package com.tambapps.http.restclient.request.handler.response;

import com.tambapps.http.restclient.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public final class RestResponseHandlers {

  private static final RestResponseHandler<String> STRING_HANDLER = new RestResponseHandler<String>() {
    @Override
    public String convert(InputStream inputStream) throws IOException {
      return IOUtils.toString(inputStream);
    }
  };

  private RestResponseHandlers() {}


  public static RestResponseHandler<String> stringHandler() {
    return STRING_HANDLER;
  }

}
