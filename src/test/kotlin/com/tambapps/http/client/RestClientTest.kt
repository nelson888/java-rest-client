package com.tambapps.http.client

import com.tambapps.http.client.request.RestRequest
import com.tambapps.http.client.request.RestRequest.Companion.builder
import com.tambapps.http.client.request.RestRequest.Companion.delete
import com.tambapps.http.client.request.RestRequest.Companion.get
import com.tambapps.http.client.request.RestRequest.Companion.put
import com.tambapps.http.client.request.body.BodyProcessors.string
import org.junit.Test

class RestClientTest : AbstractRestClientTest() {
    private val client = RestClient(API_URL)

    @Test
    fun test() {
        val request: RestRequest = get("posts/1")
                .build()
        val response = client.execute(request, RESPONSE_HANDLER)
        getAsserts(response)
    }

    @Test
    fun putTest() {
        val id = 8
        val post = Post(id, 43, "title", "body")
        val request: RestRequest = put("posts/$id")
                .json()
                .body(string(GSON.toJson(post)))
                .build()
        val response = client.execute(request, RESPONSE_HANDLER)
        putAsserts(response, post)
    }

    @Test
    fun postTest() {
        val post = Post(0, 43, "title", "body")
        val request: RestRequest = RestRequest.post("posts/")
                .json()
                .body(string(GSON.toJson(post)))
                .build()
        val response = client.execute(request, RESPONSE_HANDLER)
        postAsserts(response, post)
    }

    @Test
    fun deleteTest() {
        val response = client.execute(delete("posts/1")
                .build(), RESPONSE_HANDLER)
        deleteAsserts(response)
    }
}