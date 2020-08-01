package com.tambapps.http.client

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tambapps.http.client.response.RestResponse
import com.tambapps.http.client.response.handler.ResponseHandlers.`object`
import com.tambapps.http.client.response.handler.ResponseHandlers.objectList
import com.tambapps.http.client.util.ObjectListParser
import com.tambapps.http.client.util.ObjectParser
import junit.framework.Assert.assertEquals
import org.junit.Assert
import java.util.*

abstract class AbstractRestClientTest {
    fun getAsserts(response: RestResponse<Post>) {
        Assert.assertTrue("Should be successful", response.isSuccessful)
        Assert.assertFalse("Shouldn't be an error code", response.isErrorResponse)
        assertPost(response.data)
    }

    private fun assertPost(post: Post) {
        Assert.assertNotNull("Shouldn't be null", post)
        Assert.assertNotNull("Shouldn't be null", post.id)
        Assert.assertNotNull("Shouldn't be null", post.userId)
        Assert.assertNotNull("Shouldn't be null", post.body)
        Assert.assertNotNull("Shouldn't be null", post.title)
    }

    fun getListAsserts(response: RestResponse<List<Post>>) {
        Assert.assertTrue("Should be successful", response.isSuccessful)
        Assert.assertFalse("Shouldn't be an error code", response.isErrorResponse)
        for (post in response.data) {
            assertPost(post)
        }
    }

    fun getListAssertsWithUserId(response: RestResponse<List<Post>>, userId: Int) {
        getListAsserts(response)
        for (post in response.data) {
            Assert.assertEquals("Should be equal", userId, post.userId)
        }
    }

    fun putAsserts(response: RestResponse<Post?>, post: Post?) {
        Assert.assertFalse("Shouldn't be an error code", response.isErrorResponse)
        Assert.assertTrue("Should be successful", response.isSuccessful)
        assertEquals("Should be equal", post, response.data)
    }

    fun postAsserts(response: RestResponse<Post>, post: Post) {
        Assert.assertTrue("Should be successful", response.isSuccessful)
        Assert.assertFalse("Shouldn't be null", response.isErrorResponse)
        val responseData = response.data
        Assert.assertEquals("Should be equal", post.userId, responseData.userId)
        Assert.assertEquals("Should be equal", post.title, responseData.title)
        Assert.assertEquals("Should be equal", post.body, responseData.body)
    }

    fun deleteAsserts(response: RestResponse<Post?>) {
        Assert.assertTrue("Should be successful", response.isSuccessful)
        Assert.assertFalse("Shouldn't be an error code", response.isErrorResponse)
    }

    class Post(val id: Int, val userId: Int, val title: String, val body: String) {

        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val post = o as Post
            return id == post.id &&
                    userId == post.userId &&
                    title == post.title &&
                    body == post.body
        }

        override fun hashCode(): Int {
            return Objects.hash(id)
        }

        override fun toString(): String {
            return "Post{" +
                    "id=" + id +
                    ", userId=" + userId +
                    ", title='" + title + '\'' +
                    ", body='" + body + '\'' +
                    '}'
        }

    }

    companion object {
        const val API_URL = "https://jsonplaceholder.typicode.com/"
        const val TIMEOUT = 8
        @JvmField
        val GSON = Gson()
        private val JSON_PARSER: ObjectParser = object : ObjectParser {
            override fun <T> parse(clazz: Class<T>, data: String): T {
                return GSON.fromJson(data, clazz)
            }
        }
        @JvmField
        val RESPONSE_HANDLER = `object`(Post::class.java, JSON_PARSER)
        private val POST_LIST_TYPE = object : TypeToken<List<Post?>?>() {}.type
        private val JSON_LIST_PARSER: ObjectListParser = object : ObjectListParser {
            override fun <T> parse(clazz: Class<T>, data: String): List<T> {
                return GSON.fromJson(data, POST_LIST_TYPE)
            }
        }
        @JvmField
        val LIST_RESPONSE_HANDLER = objectList(Post::class.java, JSON_LIST_PARSER)
    }
}