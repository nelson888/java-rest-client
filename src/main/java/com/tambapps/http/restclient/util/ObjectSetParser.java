package com.tambapps.http.restclient.util;

import java.util.Set;

/**
 * Class that can parse a set of objects from a string in a given format. For example JSON, XML, ...
 */
public interface ObjectSetParser {

  /**
   * Parse an object set of a given class from the string
   * @param clazz the class of the object
   * @param data the string representing an object
   * @param <T> the type f the object
   * @return the converted object from the string
   */
  <T> Set<T> parse(Class<T> clazz, String data);

}