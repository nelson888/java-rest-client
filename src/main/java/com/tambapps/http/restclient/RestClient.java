package com.tambapps.http.restclient;

import java.io.File;
import java.io.InputStream;

public interface RestClient {

  String PUT = "PUT";
  String POST = "POST";
  String DELETE = "DELETE";

  int REQUEST_NOT_COMPLETED = -1;

  void getObject(String endPoint, Callback callback);
  void simpleRequest(String method, String endPoint, Callback callback);
  void outputRequest(String method, String endPoint, String jsonData, Callback callback);
  void putObject(String endPoint, String jsonData, Callback callback);
  void postObject(String endPoint, String jsonData, Callback callback);


  //TODO to test
  void getFile(String endPoint, InputStreamCallback callback, Callback onError);
  void fileUploadRequest(String method, String endPoint, File file, Callback callback);
  void fileUploadRequest(String method, String endPoint, File file, String key, Callback callback);
  void putFile(String endPoint, File file, Callback callback);
  void putFile(String endPoint, File file, String key, Callback callback);
  void postFile(String endPoint, File file, Callback callback);

  void setJwt(String jwt);
  void removeJwt();

  void shutdown();

  interface Callback {
    void onResponse(int responseCode, String data);
  }
  interface InputStreamCallback {
    void onResponse(int responseCode, InputStream data);
  }
}
