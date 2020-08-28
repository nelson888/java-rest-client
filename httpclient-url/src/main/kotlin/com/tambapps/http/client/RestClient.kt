package com.tambapps.http.client

import java.net.MalformedURLException
import java.net.URL

/**
 * Class that sends synchronous Http requests to a rest service
 * every request will be prefixed by the base url, meaning that
 * Request only contain the endpoint of the REST service
 */
class RestClient
/**
 *
 * @param baseUrl the base url of the rest api
 */
(baseUrl: String) : AbstractUrlHttpClient() {

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