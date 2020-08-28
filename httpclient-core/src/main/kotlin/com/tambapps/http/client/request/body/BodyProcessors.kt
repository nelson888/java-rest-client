package com.tambapps.http.client.request.body

import com.tambapps.http.client.response.HttpHeaders
import com.tambapps.http.client.util.IOUtils
import com.tambapps.http.client.util.ISSupplier
import java.io.*
import java.net.URLConnection

/**
 * Util class implementing different [BodyProcessor]
 */
object BodyProcessors {

    @JvmStatic
    fun string(content: String): BodyProcessor {
        return StringBodyProcessor(content)
    }

    @JvmOverloads
    fun multipartFile(file: File, key: String = file.name): BodyProcessor {
        return MultipartFileBodyProcessor(file, key)
    }

    @JvmOverloads
    fun multipartBytes(bytes: ByteArray, name: String, key: String = name): BodyProcessor {
        return MultipartBytesBodyProcessor(bytes, name, key)
    }

    @JvmStatic
    fun multipartStream(isSupplier: ISSupplier, name: String): BodyProcessor {
        return multipartStream(isSupplier, name, name)
    }

    @JvmStatic
    fun multipartStream(isSupplier: ISSupplier, name: String, key: String): BodyProcessor {
        return MultipartInputStreamBodyProcessor(isSupplier, key, name)
    }

    @JvmStatic
    fun bytes(bytes: ByteArray): BodyProcessor {
        return BytesBodyProcessor(bytes)
    }

    @JvmStatic
    fun stream(isSupplier: ISSupplier): BodyProcessor {
        return InputStreamBodyProcessor(isSupplier)
    }

    @JvmStatic
    fun file(file: File): BodyProcessor {
        return FileBodyProcessor(file)
    }

    private class StringBodyProcessor(private val content: String) : BodyProcessor {
        @Throws(IOException::class)
        override fun writeInto(oStream: OutputStream) {
            oStream.writer().use { wr ->
                wr.write(content)
                wr.flush()
            }
        }
    }

    private abstract class MultipartBodyProcessor internal constructor(private val name: String, private val key: String) : BodyProcessor {
        private val boundary = "*****"
        private val crlf = "\r\n"
        private val twoHyphens = "--"

        @Throws(IOException::class)
        override fun writeInto(oStream: OutputStream) {
            DataOutputStream(
                    oStream).use { request ->
                request.writeBytes(twoHyphens + boundary + crlf)
                request.writeBytes("Content-Disposition: form-data; name=\"" +
                        key + "\";filename=\"" +
                        name + "\"" + crlf)
                request.writeBytes(crlf)
                writeMultipart(request)
                request.writeBytes(crlf)
                request.writeBytes(twoHyphens + boundary + twoHyphens + crlf)
                request.flush()
            }
        }

        @Throws(IOException::class)
        abstract fun writeMultipart(request: DataOutputStream)

        override fun headers(): Map<String, String> {
            return mapOf(
                    Pair("Connection", "Keep-Alive"),
                    Pair("Cache-Control", "no-cache"),
                    Pair(HttpHeaders.CONTENT_TYPE_HEADER, "multipart/form-data;boundary=$boundary"))
        }
    }

    private abstract class MultipartStreamBodyProcessor internal constructor(name: String, key: String) : MultipartBodyProcessor(name, key) {
        @Throws(IOException::class)
        override fun writeMultipart(request: DataOutputStream) {
            inputStream.use { iStream -> IOUtils.copy(iStream, request) }
        }

        @get:Throws(IOException::class)
        abstract val inputStream: InputStream
    }

    private class MultipartFileBodyProcessor internal constructor(private val file: File, key: String) : MultipartStreamBodyProcessor(file.name, key) {
        @get:Throws(IOException::class)
        override val inputStream: InputStream
            get() = FileInputStream(file)
    }

    private class MultipartInputStreamBodyProcessor internal constructor(private val isSupplier: ISSupplier, name: String, key: String) : MultipartStreamBodyProcessor(name, key) {
        @get:Throws(IOException::class)
        override val inputStream: InputStream
            get() = isSupplier.get()
    }

    private class MultipartBytesBodyProcessor internal constructor(private val bytes: ByteArray,
                                                                           name: String, key: String) : MultipartBodyProcessor(name, key) {
        @Throws(IOException::class)
        override fun writeMultipart(request: DataOutputStream) {
            request.write(bytes)
        }
    }

    private class BytesBodyProcessor(private val bytes: ByteArray) : BodyProcessor {

        override fun writeInto(oStream: OutputStream) {
            oStream.write(bytes, 0, bytes.size)
        }
    }

    private class FileBodyProcessor(private val file: File) : BodyProcessor {
        @Throws(IOException::class)
        override fun writeInto(oStream: OutputStream) {
            FileInputStream(file).use { iStream -> IOUtils.copy(iStream, oStream) }
        }
    }

    private class InputStreamBodyProcessor(private val supplier: ISSupplier) : BodyProcessor {
        @Throws(IOException::class)
        override fun writeInto(oStream: OutputStream) {
            supplier.get().use { iStream -> IOUtils.copy(iStream, oStream) }
        }
    }
}