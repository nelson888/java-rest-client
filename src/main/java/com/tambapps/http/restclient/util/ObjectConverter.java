package com.tambapps.http.restclient.util;

/**
 * Class that can make the conversion between a string format and an object type
 */
public interface ObjectConverter extends ObjectParser {

  /**
   * Converts the object into a string format
   * @param object the object to format
   * @return the converted string
   */
  String stringify(Object object);

}
