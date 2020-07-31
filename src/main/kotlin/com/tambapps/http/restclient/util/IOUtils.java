package com.tambapps.http.restclient.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public final class IOUtils {

  public static int DEFAULT_BUFFER_SIZE = 1024;

  private IOUtils() {}

  /**
   * Reads an inputstream and convert its content to a String
   * @param stream the input stream
   * @return the string represented by the input stream
   * @throws IOException in case of I/O error
   */
  public static String toString(InputStream stream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuilder builder = new StringBuilder();
    String output;
    while ((output = reader.readLine()) != null) {
      builder.append(output);
    }
    return builder.toString();
  }

  /**
   * Whether the http response code is an error code
   * @param responseCode the http response code
   * @return whether the http response code is an error code
   */
  public static boolean isErrorCode(int responseCode) {
    return responseCode < 200 || responseCode >= 300;
  }

  /**
   * Copy an input stream content onto an output stream
   * @param is the input stream
   * @param os the output stream
   * @param bufferSize the buffer size
   * @throws IOException in case of I/O error
   */
  public static void copy(InputStream is, OutputStream os, int bufferSize) throws IOException {
    byte[] buffer = new byte[bufferSize];
    int bytesRead;
    while ((bytesRead = is.read(buffer)) != -1) {
      os.write(buffer, 0, bytesRead);
    }
  }

  /**
   * Copy an input stream content onto an output stream
   * @param is the input stream
   * @param os the output stream
   * @throws IOException in case of I/O error
   */
  public static void copy(InputStream is, OutputStream os) throws IOException {
    copy(is, os, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Reads an input stream to retrieve all the bytes
   * @param is the input stream
   * @return a container containing all the bytes of the inputstream
   * @throws IOException in case of I/O error
   */
  public static BytesContainer toByteArray(InputStream is) throws IOException {
    return toByteArray(is, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Reads an input stream to retrieve all the bytes
   * @param is the input stream
   * @return a byte array containing all the bytes of the input stream
   * @throws IOException in case of I/O error
   */
  public static byte[] toBytes(InputStream is) throws IOException {
    return toBytes(is, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Reads an input stream to retrieve all the bytes
   * @param is the input stream
   * @param bufferSize the buffer size
   * @return a container containing all the bytes of the input stream
   * @throws IOException in case of I/O error
   */
  public static BytesContainer toByteArray(InputStream is, int bufferSize) throws IOException {
    return new BytesContainer(toBytes(is, bufferSize));
  }

  /**
   * Reads an input stream to retrieve all the bytes
   * @param is the input stream
   * @param bufferSize the buffer size
   * @return a byte array containing all the bytes of the input stream
   * @throws IOException in case of I/O error
   */
  public static byte[] toBytes(InputStream is, int bufferSize) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int nRead;
    byte[] data = new byte[bufferSize];

    while ((nRead = is.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }

    return data;
  }

}
