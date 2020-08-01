package com.tambapps.http.client.util;

import java.util.List;

/**
 * Class that can parse a list of object from a string in a given format. For example JSON, XML, ...
 */
public interface ObjectListParser {

  /**
   * Parse an object of a given class from the string
   * @param clazz the class of the object
   * @param data the string representing an object
   * @param <T> the type f the object
   * @return the converted object from the string
   */
  <T> List<T> parse(Class<T> clazz, String data);

}
