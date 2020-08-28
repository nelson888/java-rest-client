package com.tambapps.http.client.response

import com.tambapps.http.client.response.handler.ResponseHandler

class SuccessResponse<T> internal constructor(responseCode: Int, headers: HttpHeaders, private val myData: T?) : AbstractResponse<T>(responseCode, headers) {

    override val isErrorResponse = false
    override val isSuccessful = true
    override val hasData = myData != null
    override val data: T
        get() =  if (hasData)  myData!!
        else throw IllegalAccessException("Success response doesn't have error data")

    override fun <ErrorT> getErrorData(responseHandler: ResponseHandler<ErrorT>): ErrorT {
        throw IllegalAccessException("Success response doesn't have error data")
    }

    override val rawErrorData: ByteArray
        get() = throw IllegalAccessException("Success response doesn't have error data")

}