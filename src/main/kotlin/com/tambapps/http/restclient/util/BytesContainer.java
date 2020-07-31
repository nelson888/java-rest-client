package com.tambapps.http.restclient.util;

/**
 * Class used to hold a byte array
 */
public class BytesContainer {

  private final byte[] bytes;

  public BytesContainer(byte[] bytes) {
    this.bytes = bytes;
  }

  public byte[] getBytes() {
    return bytes;
  }
}
