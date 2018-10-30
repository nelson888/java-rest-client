package com.tambapps.http.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public final class IOUtils {

  public static int DEFAULT_BUFFER_SIZE = 1024;

  private IOUtils() {
  }

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
    return responseCode < 200 || responseCode >= 300;
  }

  public static void copy(InputStream is, OutputStream os, int bufferSize) throws IOException {
    byte[] buffer = new byte[bufferSize];
    int bytesRead;
    while ((bytesRead = is.read(buffer)) != -1) {
      os.write(buffer, 0, bytesRead);
    }
  }

  public static void copy(InputStream is, OutputStream os) throws IOException {
    copy(is, os, DEFAULT_BUFFER_SIZE);
  }

}
