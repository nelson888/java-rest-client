package com.tambapps.http.client.auth

import java.net.HttpURLConnection

class JwtAuthentication(private val jwt: String) : Authentication {

    override fun authenticate(connection: HttpURLConnection) {
        connection.setRequestProperty("Authorization", "Bearer $jwt")
    }

}