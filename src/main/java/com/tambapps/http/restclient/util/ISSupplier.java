package com.tambapps.http.restclient.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Util class to supply an input stream
 */
public interface ISSupplier {

  InputStream get() throws IOException;

}
