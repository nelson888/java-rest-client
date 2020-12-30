package com.tambapps.http.client.auth

import java.util.Base64

class BasicAuthentication(user: String, password: String) :
        HeaderAuthentication("Authorization",
                String(Base64.getEncoder().encode("$user:$password".toByteArray())))