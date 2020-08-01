package com.tambapps.http.client

import com.tambapps.http.client.request.RestRequest
import com.tambapps.http.client.request.RestRequest.Companion.builder
import com.tambapps.http.client.request.RestRequest.Companion.delete
import com.tambapps.http.client.request.RestRequest.Companion.get
import com.tambapps.http.client.request.RestRequest.Companion.put
import com.tambapps.http.client.request.body.BodyProcessors.string
import com.tambapps.http.client.response.RestResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AsyncRestClientTest : AbstractRestClientTest() {
    private val client = AsyncRestClient(API_URL)
    private lateinit var latch //allows to wait until async code is executed
            : CountDownLatch

    @Before
    fun init() {
        latch = CountDownLatch(1)
    }

    @Throws(InterruptedException::class)
    @Test
    fun test() {
        val request: RestRequest = get("posts/1")
                .build()
        client.execute(request, RESPONSE_HANDLER) { response ->
            getAsserts(response)
            latch.countDown()
        }
        Assert.assertTrue("Should be true", latch.await(TIMEOUT.toLong(), TimeUnit.SECONDS))
    }

    @Test
    @Throws(InterruptedException::class)
    fun putTest() {
        val id = 8
        val post = Post(id, 43, "title", "body")
        val request: RestRequest = put("posts/$id")
                .body(string(GSON.toJson(post)))
                .build()
        client.execute(request, RESPONSE_HANDLER) { response ->
            putAsserts(response, post)
            latch.countDown()
        }
        Assert.assertTrue("Should be true", latch.await(TIMEOUT.toLong(), TimeUnit.SECONDS))
    }

    @Test
    @Throws(InterruptedException::class)
    fun postTest() {
        val post = Post(0, 43, "title", "body")
        val request: RestRequest = RestRequest.post("posts/")
                .json()
                .body(string(GSON.toJson(post)))
                .build()
        client.execute(request, RESPONSE_HANDLER) { response ->
            postAsserts(response, post)
            latch.countDown()
        }
        Assert.assertTrue("Should be true", latch.await(TIMEOUT.toLong(), TimeUnit.SECONDS))
    }

    @Test
    @Throws(InterruptedException::class)
    fun deleteTest() {
        val request: RestRequest = delete("posts/1")
                .build()
        client.execute(request, RESPONSE_HANDLER) { response ->
            deleteAsserts(response)
            this.latch.countDown()
        }
        Assert.assertTrue("Should be true", latch.await(TIMEOUT.toLong(), TimeUnit.SECONDS))
    }
}