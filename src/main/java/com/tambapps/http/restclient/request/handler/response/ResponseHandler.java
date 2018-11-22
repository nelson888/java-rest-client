package com.tambapps.http.restclient.request.handler.response;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface that converts a request's response to a given type
 * @param <T> the type to convert to
 */
public interface ResponseHandler<T> {

  /**
   * Convert the input stream into the given type
   * @param inputStream the request's response
   * @return the object converted from the input stream
   * @throws IOException in case f I/O exception
   */
  T convert(InputStream inputStream) throws IOException;

}
