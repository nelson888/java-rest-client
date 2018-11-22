package com.tambapps.http.restclient.util;

public interface ObjectConverter extends ObjectParser {
  <T> T parse(Class<T> clazz, String data);

  String stringify(Object object);

}
