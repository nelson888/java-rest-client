package com.tambapps.http.client

/**
 * Class that sends synchronous Http requests
 */
class HttpClient : AbstractOkHttpHttpClient() {

    override fun getUrl(endpoint: String): String {
        return endpoint
    }

}