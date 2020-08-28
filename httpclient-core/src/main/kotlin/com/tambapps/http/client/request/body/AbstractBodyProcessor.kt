package com.tambapps.http.client.request.body

import java.io.IOException
import java.io.OutputStream
import java.net.URLConnection

/**
 * Class to extend in order to implement a custom [BodyProcessor]
 */
abstract class AbstractBodyProcessor : BodyProcessor {
    @Throws(IOException::class)
    override fun prepareConnection(connection: URLConnection) {
        connection.doOutput = true
        prepareURLConnection(connection)
        connection.getOutputStream().use { oStream -> writeContent(oStream) }
    }

    @Throws(IOException::class)
    protected abstract fun writeContent(oStream: OutputStream)
    protected open fun prepareURLConnection(connection: URLConnection) {}
}