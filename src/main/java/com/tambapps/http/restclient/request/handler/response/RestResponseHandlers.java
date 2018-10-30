package com.tambapps.http.restclient.request.handler.response;

import com.tambapps.http.restclient.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class RestResponseHandlers {

  private static final RestResponseHandler<String> STRING_HANDLER = new RestResponseHandler<String>() {
    @Override
    public String convert(InputStream inputStream) throws IOException {
      return IOUtils.toString(inputStream);
    }
  };

  private RestResponseHandlers() {}


  public static RestResponseHandler<String> stringHandler() {
    return STRING_HANDLER;
  }

  public static RestResponseHandler<File> fileHandler(final File file) {
    return new RestResponseHandler<File>() {
      @Override
      public File convert(InputStream inputStream) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
          IOUtils.copy(inputStream, fos);
        }
        return file;
      }
    };
  }

  public static RestResponseHandler<File> fileHandler(final String filePath) {
    return new RestResponseHandler<File>() {
      @Override
      public File convert(InputStream inputStream) throws IOException {
        File file = new File(filePath);
        if (!file.exists() && !file.createNewFile()) {
          return null;
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
          IOUtils.copy(inputStream, fos);
        }
        return file;
      }
    };
  }

}
