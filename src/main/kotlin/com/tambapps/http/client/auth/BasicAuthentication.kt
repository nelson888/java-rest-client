package com.tambapps.http.client.auth

import java.net.HttpURLConnection
import java.util.Base64

class BasicAuthentication(user: String, password: String) : Authentication {

    private val key = String(Base64.getEncoder().encode("$user:$password".toByteArray()))

    override fun authenticate(connection: HttpURLConnection) {
        Base64.getDecoder()
        connection.setRequestProperty("Authorization", "Basic $key")
    }

}