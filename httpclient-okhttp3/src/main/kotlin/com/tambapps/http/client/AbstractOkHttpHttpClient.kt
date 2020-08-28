package com.tambapps.http.client

import com.tambapps.http.client.request.Request
import com.tambapps.http.client.response.ErrorResponse
import com.tambapps.http.client.response.HttpHeaders
import com.tambapps.http.client.response.Response
import com.tambapps.http.client.response.SuccessResponse
import com.tambapps.http.client.response.handler.ResponseHandler
import okhttp3.OkHttpClient
import java.io.ByteArrayInputStream
import java.io.InputStream
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse

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
            // TODO change BodyProcessor interface to be able to use it in okhttp
            method(request.method, null)

        }.build()

        return okHttp.newCall(okHttpRequest).execute().use { response ->
            val headers = HttpHeaders(response.headers.toMultimap())
            if (response.isSuccessful) {
                val data = successResponseHandler.convert(getInputStream(response))
                SuccessResponse(response.code, headers, data)
            } else {
                val bytes = getInputStream(response).readBytes()
                ErrorResponse(response.code, headers, bytes)
            }
        }
    }

    private fun getInputStream(response: OkHttpResponse): InputStream {
        return if (response.body != null) response.body!!.byteStream()
        else ByteArrayInputStream(ByteArray(0))
    }

}