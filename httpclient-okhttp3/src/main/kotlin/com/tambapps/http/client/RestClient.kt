package com.tambapps.http.client

/**
 * Class that sends synchronous Http requests to a rest service
 * every request will be prefixed by the base url, meaning that
 * Requests only contain the endpoint of the REST service
 */
class RestClient
/**
 *
 * @param baseUrl the base url of the rest api
 */
(baseUrl: String) : AbstractOkHttpHttpClient() {

    // ends without '/'
    private val baseUrl: String = formatBaseUrl(baseUrl)

    override fun getUrl(endpoint: String): String {
        return getRestUrl(baseUrl, endpoint)
    }
}