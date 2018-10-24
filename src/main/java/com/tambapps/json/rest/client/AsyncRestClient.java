package com.tambapps.json.rest.client;

import java.io.File;
import java.io.InputStream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncRestClient extends AbstractRestClient {

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  public AsyncRestClient(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public void fileUploadRequest(String method, String endPoint, File file, Callback<String> callback) {
    executor.submit(new PutFileRequest(file, endPoint, method, callback));
  }

  @Override
  public void getObject(String endPoint, Callback<String> callback) {
    executor.submit(new GetRequest(endPoint, callback));
  }

  @Override
  public void simpleRequest(String method, String endPoint, Callback<String> callback) {
    executor.submit(new SimpleRequest(method, endPoint, callback));
  }

  @Override
  public void outputRequest(String method, String endPoint, Object data, Callback<String> callback) {
    executor.submit(new JsonOutputRequest(data, endPoint, method, callback));
  }

  @Override
  public void getFile(String endPoint, Callback<InputStream> callback) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void shutdown() {
    executor.shutdown();
  }

}