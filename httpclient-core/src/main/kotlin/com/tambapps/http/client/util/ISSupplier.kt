package com.tambapps.http.client.util

import java.io.IOException
import java.io.InputStream

/**
 * Util class to supply an input stream
 */
interface ISSupplier {
    /**
     * get the input stream
     * @return the input stream
     * @throws IOException in case of I/O error
     */
    @Throws(IOException::class)
    fun get(): InputStream
}