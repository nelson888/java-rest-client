package com.tambapps.json.rest.client;

import java.io.File;
import java.io.InputStream;

public class BasicRestClient extends AbstractRestClient {

  public BasicRestClient(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public void fileUploadRequest(String method, String endPoint, File file, Callback<String> callback) {
    new PutFileRequest(file, endPoint, method, callback).run();
  }

  @Override
  public void getObject(String endPoint, Callback<String> callback) {
    new GetRequest(endPoint, callback).run();
  }

  @Override
  public void simpleRequest(String method, String endPoint, Callback<String> callback) {
    new SimpleRequest(method, endPoint, callback).run();
  }

  @Override
  public void outputRequest(String method, String endPoint, Object data, Callback<String> callback) {
    new JsonOutputRequest(data, endPoint, method, callback).run();
  }

  @Override
  public void getFile(String endPoint, Callback<InputStream> callback) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void shutdown() {

  }

}
