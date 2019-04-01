package com.tambapps.http.restclient.request.handler.body;

import com.tambapps.http.restclient.util.BytesContainer;
import com.tambapps.http.restclient.util.IOUtils;
import com.tambapps.http.restclient.util.ISSupplier;
import com.tambapps.http.restclient.util.ObjectConverter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLConnection;

/**
 * Util class implementing different {@link BodyHandler}
 */
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

  public static BodyHandler json(ObjectConverter converter, Object object) {
    return new JsonBodyHandler(converter.stringify(object));
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

  public static BodyHandler multipartBytes(BytesContainer bytesContainer, String name) {
    return multipartBytes(bytesContainer, name, name, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyHandler multipartBytes(BytesContainer bytesContainer, String name, String key) {
    return multipartBytes(bytesContainer, name, key, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyHandler multipartBytes(BytesContainer bytesContainer, String name, String key, int bufferSize) {
    return new MultipartByteContainerBodyHandler(bytesContainer, name, key, bufferSize);
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
    return new MultipartInputStreamBodyHandler(isSupplier, key, name, bufferSize);
  }

  public static BodyHandler bytes(byte[] bytes) {
    return new BytesBodyHandler(bytes);
  }

  public static BodyHandler stream(ISSupplier isSupplier) {
    return new InputStreamHandler(isSupplier);
  }

  public static BodyHandler file(File file) {
    return new FileBodyHandler(file);
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
        writeMultipart(request, bufferSize);
        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
      }
    }

    abstract void writeMultipart(DataOutputStream request, int bufferSize) throws IOException;

    @Override
    protected void prepareURLConnection(URLConnection connection) {
      connection.setUseCaches(false);

      connection.setRequestProperty("Connection", "Keep-Alive");
      connection.setRequestProperty("Cache-Control", "no-cache");
      connection.setRequestProperty(
          CONTENT_TYPE, "multipart/form-data;boundary=" + this.boundary);
    }

  }

  private abstract static class MultipartStreamBodyHandler extends MultipartBodyHandler {

    MultipartStreamBodyHandler(String name, String key, int bufferSize) {
      super(name, key, bufferSize);
    }

    @Override
    void writeMultipart(DataOutputStream request, int bufferSize) throws IOException {
      try (InputStream is = getInputStream()) {
        IOUtils.copy(is, request, bufferSize);
      }
    }

    abstract InputStream getInputStream() throws IOException;

  }
    private static class MultipartFileBodyHandler extends MultipartStreamBodyHandler {

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

  private static class MultipartInputStreamBodyHandler extends MultipartStreamBodyHandler {

    private final ISSupplier isSupplier;

    MultipartInputStreamBodyHandler(ISSupplier isSupplier, String name, String key, int bufferSize) {
      super(name, key, bufferSize);
      this.isSupplier = isSupplier;
    }

    @Override
    InputStream getInputStream() throws IOException {
      return isSupplier.get();
    }
  }

  private static class MultipartByteContainerBodyHandler extends MultipartBodyHandler {

    private final BytesContainer bytesContainer;

    MultipartByteContainerBodyHandler(BytesContainer bytesContainer,
                                      String name, String key, int bufferSize) {
      super(name, key, bufferSize);
      this.bytesContainer = bytesContainer;
    }

    @Override
    void writeMultipart(DataOutputStream request, int bufferSize) throws IOException {
      byte[] bytes = bytesContainer.getBytes();
      request.write(bytes);
    }
  }

  private static class BytesBodyHandler extends AbstractBodyHandler {

    private final byte[] bytes;

    public BytesBodyHandler(byte[] bytes) {
      this.bytes = bytes;
    }

    @Override
    void writeContent(OutputStream os) throws IOException {
      os.write(bytes, 0, bytes.length);
    }
  }

  private static class FileBodyHandler extends AbstractBodyHandler {

    private final File file;

    public FileBodyHandler(File file) {
      this.file = file;
    }

    @Override
    void writeContent(OutputStream os) throws IOException {
      try (InputStream is = new FileInputStream(file)) {
        IOUtils.copy(is, os);
      }
    }
  }

  private static class InputStreamHandler extends AbstractBodyHandler {

    private final ISSupplier supplier;

    public InputStreamHandler(ISSupplier supplier) {
      this.supplier = supplier;
    }

    @Override
    void writeContent(OutputStream os) throws IOException {
      try (InputStream is = supplier.get()) {
        IOUtils.copy(is, os);
      }
    }
  }
}
