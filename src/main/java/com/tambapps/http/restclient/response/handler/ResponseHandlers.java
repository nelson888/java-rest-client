package com.tambapps.http.restclient.response.handler;

import com.tambapps.http.restclient.util.BytesContainer;
import com.tambapps.http.restclient.util.IOUtils;
import com.tambapps.http.restclient.util.ObjectListParser;
import com.tambapps.http.restclient.util.ObjectParser;
import com.tambapps.http.restclient.util.ObjectSetParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * Util class implementing diferent {@link ResponseHandler}
 */
public final class ResponseHandlers {

  private static final ResponseHandler<String> STRING_HANDLER =
      new ResponseHandler<String>() {
        @Override
        public String convert(InputStream inputStream) throws IOException {
          return IOUtils.toString(inputStream);
        }
  };

  private static final ResponseHandler<BytesContainer> BYTES_HANDLER =
      new ResponseHandler<BytesContainer>() {
        @Override
        public BytesContainer convert(InputStream inputStream) throws IOException {
          return IOUtils.toByteArray(inputStream);
        }
  };

  private static final ResponseHandler<Integer> INT_HANDLER =
          new ResponseHandler<Integer>() {
            @Override
            public Integer convert(InputStream inputStream) throws IOException {
              return Integer.parseInt(STRING_HANDLER.convert(inputStream));
            }
  };

  private static final ResponseHandler<Void> NO_RESPONSE = new ResponseHandler<Void>() {
    @Override
    public Void convert(InputStream inputStream) {
      return null;
    }
  };

  private ResponseHandlers() {
  }

  public static ResponseHandler<String> stringHandler() {
    return STRING_HANDLER;
  }

  public static ResponseHandler<Integer> intHandler() {
    return INT_HANDLER;
  }

  public static <T extends Enum<T>> ResponseHandler<T> enumHandler(final Class<T> clazz) {
    return new ResponseHandler<T>() {
      @Override
      public T convert(InputStream inputStream) throws IOException {
        String name = STRING_HANDLER.convert(inputStream)
          .replace("\"", ""); //in case it is a string representation
        return Enum.valueOf(clazz, name);
      }
    };
  }

  public static ResponseHandler<BytesContainer> bytesHandler() {
    return BYTES_HANDLER;
  }

  public static ResponseHandler<BytesContainer> multipartBytesHandler() {
    return bytesHandler();
  }

  public static ResponseHandler<File> multipartFileHandler(File file) {
    return multipartFileHandler(file, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static ResponseHandler<File> multipartFileHandler(final File file,
                                                           final int bufferSize) {
    return new ResponseHandler<File>() {
      @Override
      public File convert(InputStream inputStream) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
          throw new IOException("Couldn't create new file");
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
          IOUtils.copy(inputStream, fos, bufferSize);
        }
        return file;
      }
    };
  }

  public static ResponseHandler<File> multipartFileHandler(String filePath) {
    return multipartFileHandler(filePath, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static ResponseHandler<File> multipartFileHandler(final String filePath,
                                                           final int bufferSize) {
    return new ResponseHandler<File>() {
      @Override
      public File convert(InputStream inputStream) throws IOException {
        File file = new File(filePath);
        if (!file.exists() && !file.createNewFile()) {
          return null;
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
          IOUtils.copy(inputStream, fos, bufferSize);
        }
        return file;
      }
    };
  }

  public static <T> ResponseHandler<T> objectHandler(final Class<T> tClass,
                                                     final ObjectParser parser) {
    return new ResponseHandler<T>() {
      @Override
      public T convert(InputStream inputStream) throws IOException {
        return parser.parse(tClass, STRING_HANDLER.convert(inputStream));
      }
    };
  }

  public static <T> ResponseHandler<List<T>> objectListHandler(final Class<T> tClass,
      final ObjectListParser parser) {
    return new ResponseHandler<List<T>>() {
      @Override
      public List<T> convert(InputStream inputStream) throws IOException {
        return parser.parse(tClass, STRING_HANDLER.convert(inputStream));
      }
    };
  }

  public static <T> ResponseHandler<Set<T>> objectSetHandler(final Class<T> tClass,
      final ObjectSetParser parser) {
    return new ResponseHandler<Set<T>>() {
      @Override
      public Set<T> convert(InputStream inputStream) throws IOException {
        return parser.parse(tClass, STRING_HANDLER.convert(inputStream));
      }
    };
  }

  public static ResponseHandler<Void> noResponse() {
    return NO_RESPONSE;
  }
}
