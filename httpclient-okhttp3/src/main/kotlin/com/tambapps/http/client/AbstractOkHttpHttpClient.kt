package com.tambapps.http.client

import com.tambapps.http.client.request.Request
import com.tambapps.http.client.response.Response
import com.tambapps.http.client.response.handler.ResponseHandler
import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest

/**
 * Timeouts should be set in constructor
 */
// TODO handle timeouts
abstract class AbstractOkHttpHttpClient(): AbstractHttpClient() {

    private val okHttp = OkHttpClient()


    protected abstract fun getUrl(endpoint: String): String

    override fun <T> doExecute(request: Request, successResponseHandler: ResponseHandler<T>): Response<T> {
        val okHttpRequest = OkHttpRequest.Builder().apply {
            url(getUrl(request.endpoint))
            request.headers.forEach { (name, value) -> header(name, value) }
            method(request.method, null)

        }.build()
        TODO()
    }


}