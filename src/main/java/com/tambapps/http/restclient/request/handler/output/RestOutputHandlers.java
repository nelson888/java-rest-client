package com.tambapps.http.restclient.request.handler.output;

import com.tambapps.http.restclient.IOUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLConnection;

public final class RestOutputHandlers {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String JSON_TYPE = "application/json";

  private RestOutputHandlers() {
  }

  public static RestOutputHandler string(String content) {
    return new StringOutputHandler(content);
  }

  public static RestOutputHandler json(String content) {
    return new JsonOutputHandler(content);
  }

  public static RestOutputHandler multipartFile(File file, String key, int bufferSize) {
    return new MultipartFileOutputHandler(file, key, bufferSize);
  }

  public static RestOutputHandler multipartFile(File file, String key) {
    return multipartFile(file, key, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static RestOutputHandler multipartFile(File file) {
    return multipartFile(file, file.getName());
  }

  private abstract static class AbstractOutputHandler implements RestOutputHandler {

    @Override
    public final void prepareConnection(URLConnection connection) throws IOException {
      connection.setDoOutput(true);
      writeContent(connection);
    }

    abstract void writeContent(URLConnection connection) throws IOException;
  }

  private static class StringOutputHandler extends AbstractOutputHandler {

    private final String content;

    private StringOutputHandler(String content) {
      this.content = content;
    }

    @Override
    public void writeContent(URLConnection connection) throws IOException {
      try (OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream())) {
        wr.write(content);
        wr.flush();
      }
    }
  }

  private static class JsonOutputHandler extends StringOutputHandler {

    private JsonOutputHandler(String content) {
      super(content);
    }

    @Override
    public void writeContent(URLConnection connection) throws IOException {
      connection.setRequestProperty("Accept", JSON_TYPE);
      connection.setRequestProperty(CONTENT_TYPE, JSON_TYPE);
      super.prepareConnection(connection);
    }
  }

  private static class MultipartFileOutputHandler extends AbstractOutputHandler {

    private final String boundary = "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";
    private final File file;
    private final String key;
    private final int bufferSize;

    MultipartFileOutputHandler(File file, String key, int bufferSize) {
      this.file = file;
      this.key = key;
      this.bufferSize = bufferSize;
    }

    @Override
    void writeContent(URLConnection connection) throws IOException {
      connection.setUseCaches(false);
      connection.setDoOutput(true);

      connection.setRequestProperty("Connection", "Keep-Alive");
      connection.setRequestProperty("Cache-Control", "no-cache");
      connection.setRequestProperty(
          CONTENT_TYPE, "multipart/form-data;boundary=" + this.boundary);

      String fileName = file.getName();

      try (DataOutputStream request = new DataOutputStream(
          connection.getOutputStream())) {
        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
            key + "\";filename=\"" +
            fileName + "\"" + crlf);
        request.writeBytes(crlf);
        try (FileInputStream is = new FileInputStream(file)) {
          IOUtils.copy(is, request, bufferSize);
        }
        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
      }
    }
  }
}
