package com.tambapps.http.client

import com.tambapps.http.client.auth.Authentication
import com.tambapps.http.client.request.Request
import com.tambapps.http.client.response.ErrorResponse
import com.tambapps.http.client.response.HttpHeaders
import com.tambapps.http.client.response.Response
import com.tambapps.http.client.response.SuccessResponse
import com.tambapps.http.client.response.handler.ResponseHandler
import com.tambapps.http.client.response.handler.ResponseHandlers
import com.tambapps.http.client.util.IOUtils
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

abstract class AbstractHttpClient {

    var authentication: Authentication? = null

    /**
     * Execute an http request
     * @param request the request to execute
     * @return the response of the server
     */
    fun execute(request: Request): Response<Unit> {
        return execute(request, ResponseHandlers.noResponse())
    }

    /**
     * Execute an http request
     * @param request the request to execute
     * @param responseHandler the response handler
     * @return the response of the server
     */
    fun <T> execute(request: Request, responseHandler: ResponseHandler<T>): Response<T> {
        return doExecute(request, responseHandler)
    }

    @Throws(IOException::class)
    private fun prepareConnection(request: Request): HttpURLConnection {
        val connection = getUrl(request.endpoint).openConnection() as HttpURLConnection
        connection.requestMethod = request.method
        for ((key, value) in request.headers) {
            connection.setRequestProperty(key, value)
        }
        authentication?.authenticate(connection)
        if (request.timeout != null) {
            connection.connectTimeout = request.timeout
        }
        return connection
    }

    protected abstract fun <T> doExecute(request: Request, successResponseHandler: ResponseHandler<T>): Response<T>

    @Throws(MalformedURLException::class)
    protected abstract fun getUrl(endpoint: String): URL
}