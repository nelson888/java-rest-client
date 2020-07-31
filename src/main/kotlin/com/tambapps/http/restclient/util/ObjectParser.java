package com.tambapps.http.restclient.util;

/**
 * Class that can parse an object from a string in a given format. For example JSON, XML, ...
 */
public interface ObjectParser {

  /**
   * Parse an object of a given class from the string
   * @param clazz the class of the object
   * @param data the string representing an object
   * @param <T> the type f the object
   * @return the converted object from the string
   */
  <T> T parse(Class<T> clazz, String data);
}
