package com.tambapps.http.restclient;

import java.io.File;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncRestClient extends AbstractRestClient {

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  public AsyncRestClient(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public void fileUploadRequest(String method, String endPoint, File file, String key, Callback callback) {
    executor.submit(new PutFileRequest(file, key, endPoint, method, callback));
  }

  @Override
  public void getObject(String endPoint, Callback callback) {
    executor.submit(new GetRequest(endPoint, callback));
  }

  @Override
  public void simpleRequest(String method, String endPoint, Callback callback) {
    executor.submit(new SimpleRequest(method, endPoint, callback));
  }

  @Override
  public void outputRequest(String method, String endPoint, String jsonData, Callback callback) {
    executor.submit(new JsonOutputRequest(jsonData, endPoint, method, callback));
  }

  @Override
  public void getFile(String endPoint, InputStreamCallback callback,
      Callback onError) {
    executor.submit(new GetFileRequest(endPoint, callback, onError));
  }

  @Override
  public void shutdown() {
    executor.shutdown();
  }

}