package com.tambapps.http.restclient.request.body;

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
 * Util class implementing different {@link BodyProcessor}
 */
public final class BodyProcessors {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String JSON_TYPE = "application/json";

  private BodyProcessors() { }

  public static BodyProcessor string(String content) {
    return new StringBodyProcessor(content);
  }

  public static BodyProcessor json(String content) {
    return new JsonBodyProcessor(content);
  }

  public static BodyProcessor json(ObjectConverter converter, Object object) {
    return new JsonBodyProcessor(converter.stringify(object));
  }

  public static BodyProcessor multipartFile(File file, String key, int bufferSize) {
    return new MultipartFileBodyProcessor(file, key, bufferSize);
  }

  public static BodyProcessor multipartFile(File file, String key) {
    return multipartFile(file, key, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyProcessor multipartFile(File file) {
    return multipartFile(file, file.getName());
  }

  public static BodyProcessor multipartBytes(BytesContainer bytesContainer, String name) {
    return multipartBytes(bytesContainer, name, name, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyProcessor multipartBytes(BytesContainer bytesContainer, String name, String key) {
    return multipartBytes(bytesContainer, name, key, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyProcessor multipartBytes(BytesContainer bytesContainer, String name, String key, int bufferSize) {
    return new MultipartByteContainerBodyProcessor(bytesContainer, name, key, bufferSize);
  }

  public static BodyProcessor multipartStream(ISSupplier isSupplier, String name, String key) {
    return multipartStream(isSupplier, key, name, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyProcessor multipartStream(ISSupplier isSupplier, String name) {
    return multipartStream(isSupplier, name, name, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static BodyProcessor multipartStream(ISSupplier isSupplier, String name, int bufferSize) {
    return multipartStream(isSupplier, name, name, bufferSize);
  }

  public static BodyProcessor multipartStream(ISSupplier isSupplier, String name, String key,
      int bufferSize) {
    return new MultipartInputStreamBodyProcessor(isSupplier, key, name, bufferSize);
  }

  public static BodyProcessor bytes(byte[] bytes) {
    return new BytesBodyProcessor(bytes);
  }

  public static BodyProcessor stream(ISSupplier isSupplier) {
    return new InputStreamBodyProcessor(isSupplier);
  }

  public static BodyProcessor file(File file) {
    return new FileBodyProcessor(file);
  }

  private static class StringBodyProcessor extends AbstractBodyProcessor {

    private final String content;

    private StringBodyProcessor(String content) {
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

  private static class JsonBodyProcessor extends StringBodyProcessor {

    private JsonBodyProcessor(String content) {
      super(content);
    }

    @Override
    public void prepareURLConnection(URLConnection connection) {
      connection.setRequestProperty("Accept", JSON_TYPE);
      connection.setRequestProperty(CONTENT_TYPE, JSON_TYPE);
    }

  }

  private abstract static class MultipartBodyProcessor extends AbstractBodyProcessor {

    private final String boundary = "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";
    private final String key;
    private final String name;
    private final int bufferSize;

    MultipartBodyProcessor(String name, String key, int bufferSize) {
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

  private abstract static class MultipartStreamBodyProcessor extends MultipartBodyProcessor {

    MultipartStreamBodyProcessor(String name, String key, int bufferSize) {
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
    private static class MultipartFileBodyProcessor extends MultipartStreamBodyProcessor {

    private final File file;

    MultipartFileBodyProcessor(File file, String key, int bufferSize) {
      super(file.getName(), key, bufferSize);
      this.file = file;
    }

    @Override
    InputStream getInputStream() throws IOException {
      return new FileInputStream(file);
    }
  }

  private static class MultipartInputStreamBodyProcessor extends MultipartStreamBodyProcessor {

    private final ISSupplier isSupplier;

    MultipartInputStreamBodyProcessor(ISSupplier isSupplier, String name, String key, int bufferSize) {
      super(name, key, bufferSize);
      this.isSupplier = isSupplier;
    }

    @Override
    InputStream getInputStream() throws IOException {
      return isSupplier.get();
    }
  }

  private static class MultipartByteContainerBodyProcessor extends MultipartBodyProcessor {

    private final BytesContainer bytesContainer;

    MultipartByteContainerBodyProcessor(BytesContainer bytesContainer,
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

  private static class BytesBodyProcessor extends AbstractBodyProcessor {

    private final byte[] bytes;

    public BytesBodyProcessor(byte[] bytes) {
      this.bytes = bytes;
    }

    @Override
    void writeContent(OutputStream os) throws IOException {
      os.write(bytes, 0, bytes.length);
    }
  }

  private static class FileBodyProcessor extends AbstractBodyProcessor {

    private final File file;

    public FileBodyProcessor(File file) {
      this.file = file;
    }

    @Override
    void writeContent(OutputStream os) throws IOException {
      try (InputStream is = new FileInputStream(file)) {
        IOUtils.copy(is, os);
      }
    }
  }

  private static class InputStreamBodyProcessor extends AbstractBodyProcessor {

    private final ISSupplier supplier;

    public InputStreamBodyProcessor(ISSupplier supplier) {
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
