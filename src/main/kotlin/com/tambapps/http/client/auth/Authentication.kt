package com.tambapps.http.client.auth

import java.net.HttpURLConnection

interface Authentication {

    fun authenticate(connection: HttpURLConnection)
}