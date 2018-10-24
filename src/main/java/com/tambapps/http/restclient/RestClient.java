package com.tambapps.http.restclient;

import java.io.File;
import java.io.InputStream;

public interface RestClient {

  String PUT = "PUT";
  String POST = "POST";
  String DELETE = "DELETE";

  int REQUEST_NOT_COMPLETED = -1;

  void getObject(String endPoint, Callback<String> callback);
  void simpleRequest(String method, String endPoint, Callback<String> callback);
  void outputRequest(String method, String endPoint, String jsonData, Callback<String> callback);
  void putObject(String endPoint, String jsonData, Callback<String> callback);
  void postObject(String endPoint, String jsonData, Callback<String> callback);


  //TODO to test
  void getFile(String endPoint, Callback<InputStream> callback, Callback<String> onError);
  void fileUploadRequest(String method, String endPoint, File file, Callback<String> callback);
  void putFile(String endPoint, File file, Callback<String> callback);
  void postFile(String endPoint, File file, Callback<String> callback);

  void setJwt(String jwt);
  void removeJwt();

  void shutdown();

  interface Callback<T> {
    void call(int responseCode, T data);
  }
}
