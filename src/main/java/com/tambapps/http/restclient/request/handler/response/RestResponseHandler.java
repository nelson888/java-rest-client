package com.tambapps.http.restclient.request.handler.response;

import java.io.IOException;
import java.io.InputStream;

public interface RestResponseHandler<T> {

  T convert(InputStream inputStream) throws IOException;

}
