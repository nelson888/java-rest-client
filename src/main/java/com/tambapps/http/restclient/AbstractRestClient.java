package com.tambapps.http.restclient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class AbstractRestClient implements RestClient {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String JSON_TYPE = "application/json";

  private final String baseUrl;
  private String jwt;

  public AbstractRestClient(String baseUrl) {
    this.baseUrl = baseUrl;
    jwt = null;
  }

  @Override
  public void putFile(String endPoint, File file, Callback<String> callback) {
    fileUploadRequest(PUT, endPoint, file, callback);
  }

  @Override
  public void putObject(String endPoint, String jsonData, Callback<String> callback) {
    outputRequest(PUT, endPoint, jsonData, callback);
  }

  @Override
  public void postFile(String endPoint, File file, Callback<String> callback) {
    fileUploadRequest(POST, endPoint, file, callback);
  }

  @Override
  public void postObject(String endPoint, String jsonData, Callback<String> callback) {
    outputRequest(POST, endPoint, jsonData, callback);
  }

  private abstract class HttpRequest implements Runnable {

    final String url;

    HttpRequest(String endpoint) {
      this.url = baseUrl + endpoint;
    }

    @Override
    public final void run() {
      HttpURLConnection connection;
      try {
        URL u = new URL(url);
        connection= (HttpURLConnection) u.openConnection();
        prepareRequest(connection);
        if (jwt != null) {
          connection.setRequestProperty("Authorization", "Bearer " + jwt);
        }

      } catch (IOException e) {
        onError(e);
        return;
      }

      try {
        int responseCode = connection.getResponseCode();

        try (InputStream stream = IOUtils.isErrorCode(responseCode) ?
            connection.getErrorStream() :
            connection.getInputStream()) {
          onResponse(responseCode, stream);
        }

      } catch (IOException e) {
        onError(e);
      } finally {
        connection.disconnect();
      }
    }

    abstract void prepareRequest(HttpURLConnection connection) throws IOException;
    abstract void onResponse(int responseCode, InputStream stream) throws IOException;
    abstract void onError(Exception e);
  }

  private abstract class JsonRequest extends HttpRequest {

    private Callback<String> callback;

    JsonRequest(String endpoint, Callback<String> callback) {
      super(endpoint);
      this.callback = callback;
    }

    @Override
    void onResponse(int responseCode, InputStream stream) throws IOException {
      callback.onResponse(responseCode, IOUtils.toString(stream));
    }

    @Override
    void onError(Exception e) {
      callback.onResponse(REQUEST_NOT_COMPLETED, e.getMessage());
    }
  }

  class GetRequest extends JsonRequest {
    GetRequest(String endpoint, Callback<String> callback) {
      super(endpoint, callback);
    }

    @Override
    void prepareRequest(HttpURLConnection connection) {
      //by default, the request is a get
    }
  }

  class SimpleRequest extends JsonRequest {

    private final String method;

    SimpleRequest(String method, String endpoint, Callback<String> callback) {
      super(endpoint, callback);
      this.method = method;
    }

    @Override
    void prepareRequest(HttpURLConnection connection) throws IOException {
      connection.setRequestMethod(method);
    }
  }

  class JsonOutputRequest extends JsonRequest {

    private final String data;
    private final String method;

    JsonOutputRequest(String data, String endpoint, String method, Callback<String> callback) {
      super(endpoint, callback);
      this.data = data;
      this.method = method;
    }

    @Override
    void prepareRequest(HttpURLConnection connection) throws IOException {

      connection.setRequestProperty("Accept", JSON_TYPE);
      connection.setRequestProperty(CONTENT_TYPE, JSON_TYPE);

      connection.setRequestMethod(method);
      connection.setDoOutput(true);

      try (OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream())) {
        wr.write(data);
        wr.flush();
      }
    }
  }

  class PutFileRequest extends JsonRequest {

    private final String boundary =  "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";
    private final File file;
    private final String method;

    PutFileRequest(File file, String endpoint, String method, Callback<String> callback) {
      super(endpoint, callback);
      this.file = file;
      this.method = method;
    }

    @Override
    void prepareRequest(HttpURLConnection connection) throws IOException {
      connection.setUseCaches(false);
      connection.setDoOutput(true);

      connection.setRequestMethod(method);
      connection.setRequestProperty("Connection", "Keep-Alive");
      connection.setRequestProperty("Cache-Control", "no-cache");
      connection.setRequestProperty(
          CONTENT_TYPE, "multipart/form-data;boundary=" + this.boundary);

      String fileName = file.getName();

      try (DataOutputStream request = new DataOutputStream(
          connection.getOutputStream())) {
        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
            fileName + "\";filename=\"" +
            fileName + "\"" + crlf);
        request.writeBytes(crlf);
        try (FileInputStream is = new FileInputStream(file)) {
          //TODO WRITE FILE into stream
          int b;
          while ((b = is.read()) != -1) {
            request.write(b);
          }
          //useful link: https://stackoverflow.com/questions/11766878/sending-files-using-post-with-httpurlconnection
        }
        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
      }
    }

  }

  class GetFileRequest extends HttpRequest {

    private final Callback<InputStream> callback;
    private final Callback<String> onError;

    GetFileRequest(String endpoint, Callback<InputStream> callback, Callback<String> onError) {
      super(endpoint);
      this.callback = callback;
      this.onError = onError;
    }

    @Override
    void prepareRequest(HttpURLConnection connection) throws IOException {

    }

    @Override
    void onResponse(int responseCode, InputStream stream) throws IOException {
      callback.onResponse(responseCode, stream); //TODO NOT GOOD LOOKS LIKE FILE IS IN A STRING, CREATE A FILEIMAGE FORMAT
    }

    @Override
    void onError(Exception e) {
      onError.onResponse(REQUEST_NOT_COMPLETED, e.getMessage());
    }
  }

  @Override
  public void setJwt(String jwt) {
    this.jwt = jwt;
  }

  @Override
  public void removeJwt() {
    setJwt(null);
  }

}
