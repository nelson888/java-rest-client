package com.tambapps.http.client.response

abstract class AbstractRestResponse<T>(override val responseCode: Int, override val headers: HttpHeaders) : RestResponse<T>