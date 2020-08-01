package com.tambapps.http.client.response

import com.tambapps.http.client.response.handler.ResponseHandler
import java.io.ByteArrayInputStream

class ErrorResponse<T> constructor(responseCode: Int, headers: HttpHeaders, override val rawErrorData: ByteArray) : AbstractResponse<T>(responseCode, headers) {

    constructor(rawErrorData: ByteArray): this(Response.REQUEST_NOT_SENT, HttpHeaders(emptyMap()), rawErrorData)

    override val isErrorResponse = true
    override val isSuccessful = false
    override val hasBody = rawErrorData.isNotEmpty()

    override val data: T get() = throw IllegalAccessException("Error response doesn't have error data")

    override fun <ErrorT> getErrorData(responseHandler: ResponseHandler<ErrorT>): ErrorT {
        return ByteArrayInputStream(rawErrorData).use { iStream ->  responseHandler.convert(iStream) }
    }

}