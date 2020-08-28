package com.tambapps.http.client.request.body

import java.io.IOException
import java.net.URLConnection

/**
 * Interface that writes request content into an output stream
 */
// TODO change interface
// make method write(OutputStream)
//      get mimeType
interface BodyProcessor {
    /**
     * Prepare and write the body content on the url connection
     * @param connection the connection to write to
     * @throws IOException exceptions can occur when writing to connection body stream
     */
    @Throws(IOException::class)
    fun prepareConnection(connection: URLConnection)
}