package com.tambapps.http.client

import com.google.gson.Gson
import com.tambapps.http.client.response.Response
import com.tambapps.http.client.response.handler.ResponseHandler
import junit.framework.Assert.assertEquals
import org.junit.Assert
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

abstract class AbstractTest {
    fun getAsserts(response: Response<Post>) {
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

    fun getListAsserts(response: Response<List<Post>>) {
        Assert.assertTrue("Should be successful", response.isSuccessful)
        Assert.assertFalse("Shouldn't be an error code", response.isErrorResponse)
        for (post in response.data) {
            assertPost(post)
        }
    }

    fun getListAssertsWithUserId(response: Response<List<Post>>, userId: Int) {
        getListAsserts(response)
        for (post in response.data) {
            Assert.assertEquals("Should be equal", userId, post.userId)
        }
    }

    fun putAsserts(response: Response<Post>, post: Post) {
        Assert.assertFalse("Shouldn't be an error code", response.isErrorResponse)
        Assert.assertTrue("Should be successful", response.isSuccessful)
        assertEquals("Should be equal", post, response.data)
    }

    fun postAsserts(response: Response<Post>, post: Post) {
        Assert.assertTrue("Should be successful", response.isSuccessful)
        Assert.assertFalse("Shouldn't be null", response.isErrorResponse)
        val responseData = response.data
        Assert.assertEquals("Should be equal", post.userId, responseData.userId)
        Assert.assertEquals("Should be equal", post.title, responseData.title)
        Assert.assertEquals("Should be equal", post.body, responseData.body)
    }

    fun deleteAsserts(response: Response<Post>) {
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
        const val API_URL = "https://jsonplaceholder.typicode.com"
        @JvmField
        val GSON = Gson()
        @JvmField
        val RESPONSE_HANDLER = object : ResponseHandler<Post> {
            override fun convert(inputStream: InputStream): Post {
                return GSON.fromJson(InputStreamReader(inputStream), Post::class.java)
            }
        }
    }
}