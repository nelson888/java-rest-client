package com.tambapps.http.client.request.body

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

class OkHttpRequestBody(private val bodyProcessor: BodyProcessor): RequestBody() {

    override fun contentType(): MediaType? {
        return null
    }

    override fun writeTo(sink: BufferedSink) {
        sink.outputStream().use(bodyProcessor::writeInto)
    }
}