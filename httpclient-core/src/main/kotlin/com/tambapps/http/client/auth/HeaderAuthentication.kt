package com.tambapps.http.client.auth

open class HeaderAuthentication(val header: String, val value: String) {
    override fun toString(): String {
        return "${javaClass.simpleName}('$header'=>'$value')"
    }
}