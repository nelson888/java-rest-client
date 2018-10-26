package com.tambapps.http.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class IOUtils {

  private IOUtils() {}

  public static String toString(InputStream stream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuilder builder = new StringBuilder();
    String output;
    while ((output = reader.readLine()) != null) {
      builder.append(output);
    }
    return builder.toString();
  }

  public static boolean isErrorCode(int responseCode) {
    return responseCode < 200 || responseCode>= 300;
  }

}
