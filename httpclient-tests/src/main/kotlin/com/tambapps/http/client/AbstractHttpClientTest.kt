package com.tambapps.http.client

import com.tambapps.http.client.request.Request
import com.tambapps.http.client.request.Request.Companion.delete
import com.tambapps.http.client.request.Request.Companion.get
import com.tambapps.http.client.request.Request.Companion.put
import com.tambapps.http.client.request.body.BodyProcessors.string
import org.junit.Test

abstract class AbstractHttpClientTest : AbstractTest() {

    protected abstract val client: AbstractHttpClient

    @Test
    fun test() {
        val request: Request = get("$API_URL/posts/1")
                .build()
        val response = client.execute(request, RESPONSE_HANDLER)
        getAsserts(response)
    }

    @Test
    fun putTest() {
        val id = 8
        val post = Post(id, 43, "title", "body")
        val request: Request = put("$API_URL/posts/$id")
                .json()
                .body(string(GSON.toJson(post)))
                .build()
        val response = client.execute(request, RESPONSE_HANDLER)
        putAsserts(response, post)
    }

    @Test
    fun postTest() {
        val post = Post(0, 43, "title", "body")
        val request: Request = Request.post("$API_URL/posts/")
                .json()
                .body(string(GSON.toJson(post)))
                .build()
        val response = client.execute(request, RESPONSE_HANDLER)
        postAsserts(response, post)
    }

    @Test
    fun deleteTest() {
        val response = client.execute(delete("$API_URL/posts/1")
                .build(), RESPONSE_HANDLER)
        deleteAsserts(response)
    }
}