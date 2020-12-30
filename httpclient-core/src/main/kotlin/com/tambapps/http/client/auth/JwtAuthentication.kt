package com.tambapps.http.client.auth


class JwtAuthentication(jwt: String) :
        HeaderAuthentication("Authorization", "Bearer $jwt")