package com.tambapps.http.restclient.request.handler.body;

import java.io.IOException;
import java.net.URLConnection;

/**
 * Interface that configure an URLConnection to put a body on the request
 */
public interface BodyHandler {

  /**
   * Prepare and write the body content on the url connection
   * @param connection the connection to write to
   * @throws IOException exceptions can occur when writing to connection body stream
   */
  void prepareConnection(URLConnection connection) throws IOException;

}
