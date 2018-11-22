package com.tambapps.http.restclient.request.handler.output;

import com.tambapps.http.restclient.util.IOUtils;
import com.tambapps.http.restclient.util.ISSupplier;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLConnection;

public final class BodyHandlers {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String JSON_TYPE = "application/json";

  private BodyHandlers() { }

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

  public static BodyHandler multipartStream(ISSupplier isSupplier, String name, String key) {
    return multipartStream(isSupplier, key, name, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyHandler multipartStream(ISSupplier isSupplier, String name) {
    return multipartStream(isSupplier, name, name, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyHandler multipartStream(ISSupplier isSupplier, String name, int bufferSize) {
    return multipartStream(isSupplier, name, name, bufferSize);
  }

  public static BodyHandler multipartStream(ISSupplier isSupplier, String name, String key,
      int bufferSize) {
    return new MultipartStreamBodyHandler(isSupplier, key, name, bufferSize);
  }

  private static class StringBodyHandler extends AbstractBodyHandler {

    private final String content;

    private StringBodyHandler(String content) {
      this.content = content;
    }

    @Override
    public void writeContent(OutputStream oStream) throws IOException {
      try (OutputStreamWriter wr = new OutputStreamWriter(oStream)) {
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
    public void prepareURLConnection(URLConnection connection) {
      connection.setRequestProperty("Accept", JSON_TYPE);
      connection.setRequestProperty(CONTENT_TYPE, JSON_TYPE);
    }

  }

  private abstract static class MultipartBodyHandler extends AbstractBodyHandler {

    private final String boundary = "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";
    private final String key;
    private final String name;
    private final int bufferSize;

    MultipartBodyHandler(String name, String key, int bufferSize) {
      this.name = name;
      this.key = key;
      this.bufferSize = bufferSize;
    }

    @Override
    void writeContent(OutputStream oStream) throws IOException {
      try (DataOutputStream request = new DataOutputStream(
          oStream)) {
        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
            key + "\";filename=\"" +
            name + "\"" + crlf);
        request.writeBytes(crlf);
        try (InputStream is = getInputStream()) {
          IOUtils.copy(is, request, bufferSize);
        }
        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
      }
    }

    @Override
    protected void prepareURLConnection(URLConnection connection) {
      connection.setUseCaches(false);

      connection.setRequestProperty("Connection", "Keep-Alive");
      connection.setRequestProperty("Cache-Control", "no-cache");
      connection.setRequestProperty(
          CONTENT_TYPE, "multipart/form-data;boundary=" + this.boundary);
    }

    abstract InputStream getInputStream() throws IOException;
  }

  private static class MultipartFileBodyHandler extends MultipartBodyHandler {

    private final File file;

    MultipartFileBodyHandler(File file, String key, int bufferSize) {
      super(file.getName(), key, bufferSize);
      this.file = file;
    }

    @Override
    InputStream getInputStream() throws IOException {
      return new FileInputStream(file);
    }
  }

  private static class MultipartStreamBodyHandler extends MultipartBodyHandler {

    private final ISSupplier isSupplier;

    MultipartStreamBodyHandler(ISSupplier isSupplier, String name, String key, int bufferSize) {
      super(name, key, bufferSize);
      this.isSupplier = isSupplier;
    }

    @Override
    InputStream getInputStream() throws IOException {
      return isSupplier.get();
    }
  }
}
