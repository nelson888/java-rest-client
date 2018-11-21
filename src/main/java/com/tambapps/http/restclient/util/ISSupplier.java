package com.tambapps.http.restclient.util;

import java.io.IOException;
import java.io.InputStream;

public interface ISSupplier {

  InputStream get() throws IOException;

}
