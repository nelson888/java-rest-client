package com.tambapps.http.client

import com.tambapps.http.client.request.Request
import com.tambapps.http.client.response.Response
import com.tambapps.http.client.response.handler.ResponseHandler
import com.tambapps.http.client.response.handler.ResponseHandlers.noResponse
import java.net.MalformedURLException
import java.net.URL

/**
 * Class that sends synchronous REST requests
 */
class RestClient
/**
 *
 * @param baseUrl the base url of the rest api
 */
(baseUrl: String) : AbstractHttpClient() {

    // ends without '/'
    private val baseUrl: String =
            if (baseUrl.endsWith("/")) baseUrl.substring(0, baseUrl.length - 1)
            else baseUrl

    @Throws(MalformedURLException::class)
    override fun getUrl(endpoint: String): URL {
        return URL(
                if (endpoint.startsWith("/")) baseUrl + endpoint
                else "$baseUrl/$endpoint"
        )
    }
}