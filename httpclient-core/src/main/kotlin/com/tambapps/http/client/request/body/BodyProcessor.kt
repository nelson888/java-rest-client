package com.tambapps.http.client.request.body

import java.io.IOException
import java.io.OutputStream
import java.net.URLConnection

/**
 * Interface that writes request content into an output stream
 */
interface BodyProcessor {

    @Throws(IOException::class)
    fun writeInto(oStream: OutputStream)

    fun headers(): Map<String, String> {
        return emptyMap()
    }
}