package com.tambapps.http.client

import com.tambapps.http.client.request.Request
import com.tambapps.http.client.response.ErrorResponse
import com.tambapps.http.client.response.HttpHeaders
import com.tambapps.http.client.response.Response
import com.tambapps.http.client.response.SuccessResponse
import com.tambapps.http.client.response.handler.ResponseHandler
import com.tambapps.http.client.util.IOUtils
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

abstract class AbstractUrlHttpClient: AbstractHttpClient() {

    @Throws(MalformedURLException::class)
    protected abstract fun getUrl(endpoint: String): URL

    @Throws(IOException::class)
    private fun prepareConnection(request: Request): HttpURLConnection {
        val connection = getUrl(request.endpoint).openConnection() as HttpURLConnection
        connection.requestMethod = request.method
        for ((key, value) in request.headers) {
            connection.setRequestProperty(key, value)
        }
        authentication?.authenticate(connection)
        if (request.timeout != null) {
            connection.connectTimeout = request.timeout as Int
        }
        return connection
    }

    override fun <T> doExecute(request: Request, successResponseHandler: ResponseHandler<T>): Response<T> {
        val connection: HttpURLConnection
        try {
            connection = prepareConnection(request)
            if (request.hasBody()) {
                val bodyProcessor = request.bodyProcessor!!
                bodyProcessor.headers().forEach { (name, value) -> connection.setRequestProperty(name, value) }
                connection.doOutput = true
                connection.outputStream.use {
                    bodyProcessor.writeInto(it)
                }
            }
        } catch (e: IOException) {
            return ErrorResponse("Error while preparing connection: ${e.message}".toByteArray())
        }
        val responseHeaders: MutableMap<String, List<String>> = HashMap()
        var responseCode = Response.REQUEST_NOT_SENT
        try {
            responseCode = connection.responseCode
            responseHeaders.putAll(connection.headerFields)
            if (IOUtils.isErrorCode(responseCode)) {
                // sometimes, Java might use inputStream even though the response code isn't a successful one
                val stream = connection.errorStream ?: connection.inputStream
                if (stream != null) {
                    return stream.use {
                        ErrorResponse(responseCode, HttpHeaders(responseHeaders), IOUtils.toBytes(it))
                    }
                } else {
                    return ErrorResponse(responseCode, HttpHeaders(responseHeaders), ByteArray(0))
                }
            } else {
                val stream = connection.inputStream
                if (stream != null) {
                    return SuccessResponse(responseCode, HttpHeaders(responseHeaders), successResponseHandler.convert(stream))
                } else {
                    return SuccessResponse(responseCode, HttpHeaders(responseHeaders), null)
                }
            }
        } catch (e: IOException) {
            return ErrorResponse(responseCode, HttpHeaders(responseHeaders), e.message!!.toByteArray())
        } finally {
            connection.disconnect()
        }
    }

}