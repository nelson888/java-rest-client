package com.tambapps.http.restclient;

import java.io.File;

public class BasicRestClient extends AbstractRestClient {

  public BasicRestClient(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public void fileUploadRequest(String method, String endPoint, File file, String key, Callback callback) {
    new PutFileRequest(file, key, endPoint, method, callback).run();
  }

  @Override
  public void getObject(String endPoint, Callback callback) {
    new GetRequest(endPoint, callback).run();
  }

  @Override
  public void simpleRequest(String method, String endPoint, Callback callback) {
    new SimpleRequest(method, endPoint, callback).run();
  }

  @Override
  public void outputRequest(String method, String endPoint, String jsonData, Callback callback) {
    new JsonOutputRequest(jsonData, endPoint, method, callback).run();
  }

  @Override
  public void getFile(String endPoint, InputStreamCallback callback,
      Callback onError) {
    new GetFileRequest(endPoint, callback, onError).run();
  }

  @Override
  public void shutdown() {

  }

}
