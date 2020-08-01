package com.tambapps.http.client.response.handler

import com.tambapps.http.client.util.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Util class implementing different [ResponseHandler]
 */
object ResponseHandlers {
    private val STRING_HANDLER: ResponseHandler<String> = object : ResponseHandler<String> {
        @Throws(IOException::class)
        override fun convert(inputStream: InputStream): String {
            return IOUtils.toString(inputStream)
        }
    }
    private val BYTES_HANDLER: ResponseHandler<BytesContainer> = object : ResponseHandler<BytesContainer> {
        @Throws(IOException::class)
        override fun convert(inputStream: InputStream): BytesContainer {
            return IOUtils.toByteArray(inputStream)
        }
    }
    private val INT_HANDLER: ResponseHandler<Int> = object : ResponseHandler<Int> {
        @Throws(IOException::class)
        override fun convert(inputStream: InputStream): Int {
            return STRING_HANDLER.convert(inputStream).toInt()
        }
    }
    private val NO_RESPONSE: ResponseHandler<Unit> = object : ResponseHandler<Unit> {
        override fun convert(inputStream: InputStream): Unit {
        }
    }

    @JvmStatic
    fun string(): ResponseHandler<String> {
        return STRING_HANDLER
    }

    @JvmStatic
    fun integer(): ResponseHandler<Int> {
        return INT_HANDLER
    }

    @JvmStatic
    fun <T : Enum<T>?> enumeration(clazz: Class<T>?): ResponseHandler<T> {
        return object : ResponseHandler<T> {
            @Throws(IOException::class)
            override fun convert(inputStream: InputStream): T {
                val name = STRING_HANDLER.convert(inputStream)
                        .replace("\"", "") //in case it is a string representation
                return java.lang.Enum.valueOf(clazz, name)
            }
        }
    }

    /**
     * Response handler returning raw response into a byte array
     * @return response handler converting to byte array
     */
    @JvmStatic
    fun bytes(): ResponseHandler<BytesContainer> {
        return BYTES_HANDLER
    }

    /**
     * Response handler returning multipart response into a byte array
     * @return response handler converting multipart response to byte array
     */
    @JvmStatic
    fun multipartBytes(): ResponseHandler<BytesContainer> {
        return bytes()
    }
    /**
     * handler writing response into a file
     * @param file the file to write the response content to
     * @param bufferSize the buffer size
     * @return response handler writing in given file
     */
    /**
     * handler writing response into a file
     * @param file the file to write the response content to
     * @return response handler writing in given file
     */
    @JvmOverloads
    fun multipartFile(file: File,
                      bufferSize: Int = IOUtils.DEFAULT_BUFFER_SIZE): ResponseHandler<File> {
        return object : ResponseHandler<File> {
            @Throws(IOException::class)
            override fun convert(inputStream: InputStream): File {
                if (!file.exists() && !file.createNewFile()) {
                    throw IOException("Couldn't create new file")
                }
                FileOutputStream(file).use { fos -> IOUtils.copy(inputStream, fos, bufferSize) }
                return file
            }
        }
    }
    /**
     * handler writing response into a file
     * @param filePath the path of the file to write the response content to
     * @param bufferSize the buffer size
     * @return response handler writing in given file
     */
    /**
     * handler writing response into a file
     * @param filePath the path of the file to write the response content to
     * @return response handler writing in given file
     */
    @JvmOverloads
    fun multipartFile(filePath: String,
                      bufferSize: Int = IOUtils.DEFAULT_BUFFER_SIZE): ResponseHandler<File> {
        return object : ResponseHandler<File> {
            @Throws(IOException::class)
            override fun convert(inputStream: InputStream): File {
                val file = File(filePath)
                file.outputStream().use { fos -> IOUtils.copy(inputStream, fos, bufferSize) }
                return file
            }
        }
    }

    /**
     * handler converting response into an object
     * @param tClass the class o the object
     * @param parser the parser
     * @param <T> the type of the object
     * @return handler converting the response into an object
    </T> */
    @JvmStatic
    fun <T> `object`(tClass: Class<T>?,
                     parser: ObjectParser): ResponseHandler<T> {
        return object : ResponseHandler<T> {
            @Throws(IOException::class)
            override fun convert(inputStream: InputStream): T {
                return parser.parse(tClass, STRING_HANDLER.convert(inputStream))
            }
        }
    }

    /**
     * handler converting response into a list of objects
     * @param tClass the class of the objects
     * @param parser the parser
     * @param <T> the type of the object
     * @return handler converting the response into a list of objects
    </T> */
    @JvmStatic
    fun <T> objectList(tClass: Class<T>?,
                       parser: ObjectListParser): ResponseHandler<List<T>> {
        return object : ResponseHandler<List<T>> {
            @Throws(IOException::class)
            override fun convert(inputStream: InputStream): List<T> {
                return parser.parse(tClass, STRING_HANDLER.convert(inputStream))
            }
        }
    }

    /**
     * handler converting response into a set of objects
     * @param tClass the class of the objects
     * @param parser the parser
     * @param <T> the type of the object
     * @return handler converting the response into a set of objects
    </T> */
    @JvmStatic
    fun <T> objectSetHandler(tClass: Class<T>?,
                             parser: ObjectSetParser): ResponseHandler<Set<T>> {
        return object : ResponseHandler<Set<T>> {
            @Throws(IOException::class)
            override fun convert(inputStream: InputStream): Set<T> {
                return parser.parse(tClass, STRING_HANDLER.convert(inputStream))
            }
        }
    }

    /**
     * handler for ignoring the response
     * @return a handler ignoring the response
     */
    @JvmStatic
    fun noResponse(): ResponseHandler<Unit> {
        return NO_RESPONSE
    }
}