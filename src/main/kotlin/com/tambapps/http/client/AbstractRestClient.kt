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

abstract class AbstractRestClient(baseUrl: String) {

    // ends without '/'
    private val baseUrl: String =
            if (baseUrl.endsWith("/")) baseUrl.substring(0, baseUrl.length - 1)
            else baseUrl
    var jwt: String? = null


    @Throws(MalformedURLException::class)
    private fun getUrl(endpoint: String): URL {
        return URL(
                if (endpoint.startsWith("/")) baseUrl + endpoint
                else "$baseUrl/$endpoint"
        )
    }

    @Throws(IOException::class)
    private fun prepareConnection(request: Request): HttpURLConnection {
        val connection = getUrl(request.endpoint).openConnection() as HttpURLConnection
        connection.requestMethod = request.method
        for ((key, value) in request.headers) {
            connection.setRequestProperty(key, value)
        }
        if (jwt != null) {
            connection.setRequestProperty("Authorization", "Bearer $jwt")
        }
        if (request.timeout != null) {
            connection.connectTimeout = request.timeout
        }
        return connection
    }

    protected open fun <T> doExecute(request: Request, successResponseHandler: ResponseHandler<T>): Response<T> {
        val connection: HttpURLConnection
        try {
            connection = prepareConnection(request)
            if (request.hasBody()) {
                request.bodyProcessor!!.prepareConnection(connection)
            }
        } catch (e: IOException) {
            return ErrorResponse(e.message!!.toByteArray())
        }
        val responseHeaders: MutableMap<String, List<String>> = HashMap()
        var responseCode = Response.REQUEST_NOT_SENT
        try {
            responseCode = connection.responseCode
            responseHeaders.putAll(connection.headerFields)
            val isErrorCode = IOUtils.isErrorCode(responseCode)
            if (isErrorCode) {
                val stream = connection.errorStream
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

    fun removeJwt() {
        this.jwt = null
    }
}