package com.tambapps.http.restclient.request;

import java.io.File;
import java.net.URL;
import java.util.Map;

public class HttpMultipartFileRequest extends AbstractRequest {

  private final File file;

  public HttpMultipartFileRequest(URL url,
      Map<String, String> headers, String method, Long timeout, File file) {
    super(url, headers, method, timeout);
    this.file = file;
  }

  public static Builder builder() {
    return new Builder();
  }

  public File getFile() {
    return file;
  }

  public boolean hasFile() {
    return  file != null;
  }

  private static class Builder extends AbstractRequest.Builder<Builder, HttpMultipartFileRequest> {

    private File file = null;

    Builder output(File file) {
      this.file = file;
      return this;
    }

    @Override
    public HttpMultipartFileRequest build() {
      return new HttpMultipartFileRequest(url, headers, method, timeout, file);
    }
  }
}
