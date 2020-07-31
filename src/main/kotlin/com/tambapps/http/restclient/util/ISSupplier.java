package com.tambapps.http.restclient.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Util class to supply an input stream
 */
public interface ISSupplier {

  /**
   * get the input stream
   * @return the input stream
   * @throws IOException in case of I/O error
   */
  InputStream get() throws IOException;

}
