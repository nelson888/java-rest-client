package com.tambapps.http.client.response

abstract class AbstractResponse<T>(override val responseCode: Int, override val headers: HttpHeaders) : Response<T>