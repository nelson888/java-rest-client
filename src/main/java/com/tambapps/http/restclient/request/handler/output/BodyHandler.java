package com.tambapps.http.restclient.request.handler.output;

import java.io.IOException;
import java.net.URLConnection;

public interface BodyHandler {

  void prepareConnection(URLConnection connection) throws IOException;

}
