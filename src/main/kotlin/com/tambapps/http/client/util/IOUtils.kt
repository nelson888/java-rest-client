package com.tambapps.http.client.util

import java.io.*

object IOUtils {
    var DEFAULT_BUFFER_SIZE = 1024

    /**
     * Reads an inputstream and convert its content to a String
     * @param stream the input stream
     * @return the string represented by the input stream
     * @throws IOException in case of I/O error
     */
    @Throws(IOException::class)
    fun toString(stream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(stream))
        val builder = StringBuilder()
        var output: String
        while (reader.readLine().also { output = it } != null) {
            builder.append(output)
        }
        return builder.toString()
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
     * @param is the input stream
     * @param os the output stream
     * @param bufferSize the buffer size
     * @throws IOException in case of I/O error
     */
    /**
     * Copy an input stream content onto an output stream
     * @param `is` the input stream
     * @param os the output stream
     * @throws IOException in case of I/O error
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun copy(iStream: InputStream, os: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE) {
        val buffer = ByteArray(bufferSize)
        var bytesRead: Int
        while (iStream.read(buffer).also { bytesRead = it } != -1) {
            os.write(buffer, 0, bytesRead)
        }
    }
    /**
     * Reads an input stream to retrieve all the bytes
     * @param is the input stream
     * @param bufferSize the buffer size
     * @return a container containing all the bytes of the input stream
     * @throws IOException in case of I/O error
     */
    /**
     * Reads an input stream to retrieve all the bytes
     * @param is the input stream
     * @param bufferSize the buffer size
     * @return a byte array containing all the bytes of the input stream
     * @throws IOException in case of I/O error
     */
    /**
     * Reads an input stream to retrieve all the bytes
     * @param `is` the input stream
     * @return a byte array containing all the bytes of the input stream
     * @throws IOException in case of I/O error
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun toBytes(iStream: InputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): ByteArray {
        val buffer = ByteArrayOutputStream()
        var nRead: Int
        val data = ByteArray(bufferSize)
        while (iStream.read(data, 0, data.size).also { nRead = it } != -1) {
            buffer.write(data, 0, nRead)
        }
        return data
    }
}