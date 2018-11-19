package com.tambapps.http.restclient.request.handler.output;

import com.tambapps.http.restclient.util.IOUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLConnection;

public final class BodyHandlers {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String JSON_TYPE = "application/json";

  private BodyHandlers() {
  }

  public static BodyHandler string(String content) {
    return new StringBodyHandler(content);
  }

  public static BodyHandler json(String content) {
    return new JsonBodyHandler(content);
  }

  public static BodyHandler multipartFile(File file, String key, int bufferSize) {
    return new MultipartFileBodyHandler(file, key, bufferSize);
  }

  public static BodyHandler multipartFile(File file, String key) {
    return multipartFile(file, key, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyHandler multipartFile(File file) {
    return multipartFile(file, file.getName());
  }

  private abstract static class AbstractBodyHandler implements BodyHandler {

    @Override
    public final void prepareConnection(URLConnection connection) throws IOException {
      connection.setDoOutput(true);
      writeContent(connection);
    }

    abstract void writeContent(URLConnection connection) throws IOException;
  }

  private static class StringBodyHandler extends AbstractBodyHandler {

    private final String content;

    private StringBodyHandler(String content) {
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

  private static class JsonBodyHandler extends StringBodyHandler {

    private JsonBodyHandler(String content) {
      super(content);
    }

    @Override
    public void writeContent(URLConnection connection) throws IOException {
      connection.setRequestProperty("Accept", JSON_TYPE);
      connection.setRequestProperty(CONTENT_TYPE, JSON_TYPE);
      super.writeContent(connection);
    }
  }

  private static class MultipartFileBodyHandler extends AbstractBodyHandler {

    private final String boundary = "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";
    private final File file;
    private final String key;
    private final int bufferSize;

    MultipartFileBodyHandler(File file, String key, int bufferSize) {
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
