package com.tambapps.http.client

import java.net.MalformedURLException
import java.net.URL

/**
 * Class that sends synchronous Http requests
 */
class HttpClient : AbstractUrlHttpClient() {

    @Throws(MalformedURLException::class)
    override fun getUrl(url: String): URL {
        return URL(url)
    }
}