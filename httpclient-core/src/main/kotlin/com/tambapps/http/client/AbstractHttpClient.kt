package com.tambapps.http.client

import com.tambapps.http.client.auth.HeaderAuthentication
import com.tambapps.http.client.request.Request
import com.tambapps.http.client.response.Response
import com.tambapps.http.client.response.handler.ResponseHandler
import com.tambapps.http.client.response.handler.ResponseHandlers

abstract class AbstractHttpClient {

    var authentication: HeaderAuthentication? = null

    // headers that will be added to all requests. They can be overrided by request headers
    protected val headers: MutableMap<String, String> = mutableMapOf()

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

    protected abstract fun <T> doExecute(request: Request, successResponseHandler: ResponseHandler<T>): Response<T>

    companion object {
        @JvmStatic
        protected fun formatBaseUrl(baseUrl: String): String {
            return if (baseUrl.endsWith("/")) baseUrl.substring(0, baseUrl.length - 1)
            else baseUrl
        }

        @JvmStatic
        protected fun getRestUrl(baseUrl: String, endpoint: String): String {
            return if (endpoint.startsWith("/")) baseUrl + endpoint
            else "$baseUrl/$endpoint"
        }
    }

    fun addHeader(name: String, value: String) {
        headers[name] = value
    }

    fun removeHeader(name: String) {
        headers.remove(name)
    }

    fun getHeader(name: String): String? {
        return headers[name]
    }
}