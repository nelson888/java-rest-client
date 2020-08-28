package com.tambapps.http.client.util

import java.io.*

object IOUtils {

    /**
     * Reads an inputstream and convert its content to a String
     * @param iStream the input stream
     * @return the string represented by the input stream
     * @throws IOException in case of I/O error
     */
    @Throws(IOException::class)
    fun toString(iStream: InputStream): String {
        return String(iStream.readBytes())
    }

    /**
     * Whether the http response code is an error code
     * @param responseCode the http response code
     * @return whether the http response code is an error code
     */
    fun isErrorCode(responseCode: Int): Boolean {
        return responseCode < 200 || responseCode >= 300
    }

    /**
     * Copy an input stream content onto an output stream
     * @param iStream the input stream
     * @param oStream the output stream
     * @throws IOException in case of I/O error
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copy(iStream: InputStream, oStream: OutputStream) {
        iStream.copyTo(oStream)
    }

    /**
     * Reads an input stream to retrieve all the bytes
     * @param iStream the input stream
     * @return a byte array containing all the bytes of the input stream
     * @throws IOException in case of I/O error
     */
    @JvmStatic
    @Throws(IOException::class)
    fun toBytes(iStream: InputStream): ByteArray {
        return iStream.readBytes()
    }
}