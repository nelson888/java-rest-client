package com.tambapps.http.client.response

import com.tambapps.http.client.response.handler.ResponseHandler

/**
 * Class representing an HTTP response
 * @param <T> the type of the response
</T> */
interface Response<T> {
    /**
     * Returns the response code
     * @return the response code
     */
    val responseCode: Int

    /**
     * Returns whether the response has an error response code or not
     * @return  whether the response has an error response code or not
     */
    val isErrorResponse: Boolean

    /**
     * Returns whether the response has a successful response code
     * @return whether the response has a successful response code
     */
    val isSuccessful: Boolean

    /**
     * Returns whether the response has a body
     * @return whether the response has a body
     */
    val hasBody: Boolean
    /**
     * Get the data of the response, if it was successful
     * @return the data
     */
    val data: T

    /**
     * Returns the error data of the response, if the response wasn't successful
     * @param responseHandler the response handler
     * @param <ErrorT> the error type
     * @return the error data
    </ErrorT> */
    fun <ErrorT> getErrorData(responseHandler: ResponseHandler<ErrorT>): ErrorT

    /**
     * Returns the raw error data in form of bytes, if the response wasn't successful
     * @return the raw error data in form of bytes
     */
    val rawErrorData: ByteArray

    /**
     * Returns the headers of the response
     * @return the headers
     */
    val headers: HttpHeaders

    companion object {
        const val REQUEST_NOT_SENT = -1
    }
}