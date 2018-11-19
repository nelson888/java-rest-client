package com.tambapps.http.restclient.util;

public interface ObjectParser {

  <T> T parse(Class<T> clazz, String data);
}
