package com.tambapps.http.restclient.request.handler.output;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;

/**
 * Class to extend in order to implement a custom {@link BodyHandler}
 */
public abstract class AbstractBodyHandler implements BodyHandler {

  @Override
  public final void prepareConnection(URLConnection connection) throws IOException {
    connection.setDoOutput(true);
    prepareURLConnection(connection);
    try (OutputStream oStream =  connection.getOutputStream()) {
      writeContent(oStream);
    }
  }

  abstract void writeContent(OutputStream oStream) throws IOException;

  protected void prepareURLConnection(URLConnection connection) {}
}