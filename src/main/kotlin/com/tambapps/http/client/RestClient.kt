package com.tambapps.http.client

import com.tambapps.http.client.request.RestRequest
import com.tambapps.http.client.response.RestResponse
import com.tambapps.http.client.response.handler.ResponseHandler
import com.tambapps.http.client.response.handler.ResponseHandlers.noResponse

/**
 * Class that sends synchronous REST requests
 */
class RestClient
/**
 *
 * @param baseUrl the base url of the rest api
 */
(baseUrl: String) : AbstractRestClient(baseUrl) {
    /**
     * Execute an http request
     * @param request the request to execute
     * @return the response of the server
     */
    fun execute(request: RestRequest): RestResponse<Unit> {
        return execute(request, noResponse())
    }

    /**
     * Execute an http request
     * @param request the request to execute
     * @param responseHandler the response handler
     * @return the response of the server
     */
    fun <T> execute(request: RestRequest, responseHandler: ResponseHandler<T>): RestResponse<T> {
        return doExecute(request, responseHandler)
    }
}