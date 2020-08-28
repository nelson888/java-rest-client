package com.tambapps.http.client.response.handler

import com.tambapps.http.client.util.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.NumberFormatException

/**
 * Util class implementing different [ResponseHandler]
 */
object ResponseHandlers {

    @JvmStatic
    fun string(): ResponseHandler<String> {
        return StringHandler()
    }

    @JvmStatic
    fun integer(): ResponseHandler<Int> {
        return IntHandler()
    }

    @JvmStatic
    fun long(): ResponseHandler<Long> {
        return LongHandler()
    }

    @JvmStatic
    fun <T : Enum<T>> enumeration(clazz: Class<T>): ResponseHandler<T> {
        return EnumHandler(clazz)
    }

    /**
     * Response handler returning raw response into a byte array
     * @return response handler converting to byte array
     */
    @JvmStatic
    fun bytes(): ResponseHandler<ByteArray> {
        return BytesHandler()
    }

    /**
     * Response handler returning multipart response into a byte array
     * @return response handler converting multipart response to byte array
     */
    @JvmStatic
    fun multipartBytes(): ResponseHandler<ByteArray> {
        return bytes()
    }

    /**
     * handler writing response into a file
     * @param file the file to write the response content to
     * @return response handler writing in given file
     */
    @JvmStatic
    fun multipartFile(file: File): ResponseHandler<File> {
        return FileHandler(file)
    }

    /**
     * handler writing response into a file
     * @param filePath the path of the file to write the response content to
     * @return response handler writing in given file
     */
    @JvmStatic
    fun multipartFile(filePath: String): ResponseHandler<File> {
        return multipartFile(File(filePath))
    }

    /**
     * handler for ignoring the response
     * @return a handler ignoring the response
     */
    @JvmStatic
    fun noResponse(): ResponseHandler<Unit> {
        return NoResponse()
    }

    private class StringHandler: ResponseHandler<String> {
        @Throws(IOException::class)
        override fun convert(inputStream: InputStream): String {
            return IOUtils.toString(inputStream)
        }
    }

    private class IntHandler: ResponseHandler<Int> {
        @Throws(IOException::class)
        override fun convert(inputStream: InputStream): Int {
            try {
                return IOUtils.toString(inputStream).toInt()
            } catch (e: NumberFormatException) {
                throw IOException("Error while parsing number", e)
            }
        }
    }

    private class LongHandler: ResponseHandler<Long> {
        @Throws(IOException::class)
        override fun convert(inputStream: InputStream): Long {
            try {
                return IOUtils.toString(inputStream).toLong()
            } catch (e: NumberFormatException) {
                throw IOException("Error while parsing number", e)
            }
        }
    }

    private class EnumHandler<T: Enum<T>>(private var clazz: Class<T>): ResponseHandler<T> {
        @Throws(IOException::class)
        override fun convert(inputStream: InputStream): T {
            val name = IOUtils.toString(inputStream)
                    .replace("\"", "") //in case it is a string representation
            return java.lang.Enum.valueOf(clazz, name)
        }
    }

    private class BytesHandler: ResponseHandler<ByteArray> {
        @Throws(IOException::class)
        override fun convert(inputStream: InputStream): ByteArray {
            return IOUtils.toBytes(inputStream)
        }
    }

    private class FileHandler(private val file: File): ResponseHandler<File> {
        @Throws(IOException::class)
        override fun convert(inputStream: InputStream): File {
            file.outputStream().use { fos -> IOUtils.copy(inputStream, fos) }
            return file
        }
    }

    private class NoResponse: ResponseHandler<Unit> {
        override fun convert(inputStream: InputStream) {
        }
    }
}