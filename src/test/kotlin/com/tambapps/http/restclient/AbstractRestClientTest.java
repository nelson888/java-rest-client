package com.tambapps.http.restclient;

import static org.junit.Assert.*;

import com.google.gson.reflect.TypeToken;
import com.tambapps.http.restclient.response.handler.ResponseHandler;
import com.tambapps.http.restclient.response.handler.ResponseHandlers;
import com.tambapps.http.restclient.response.RestResponse;
import com.tambapps.http.restclient.util.ObjectListParser;
import com.tambapps.http.restclient.util.ObjectParser;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public abstract class AbstractRestClientTest {

  static final String API_URL = "https://jsonplaceholder.typicode.com/";
  static final int TIMEOUT = 8;
  static final Gson GSON = new Gson();
  private static final ObjectParser JSON_PARSER = new ObjectParser() {
    @Override
    public <T> T parse(Class<T> clazz, String data) {
      return GSON.fromJson(data, clazz);
    }
  };
  static final ResponseHandler<Post> RESPONSE_HANDLER =
    ResponseHandlers.object(Post.class, JSON_PARSER);

  private static final Type POST_LIST_TYPE = new TypeToken<List<Post>>(){}.getType();
  private static final ObjectListParser JSON_LIST_PARSER = new ObjectListParser() {
    @Override
    public <T> List<T> parse(Class<T> clazz, String data) {
      return GSON.fromJson(data, POST_LIST_TYPE);
    }
  };
  static final ResponseHandler<List<Post>> LIST_RESPONSE_HANDLER =
    ResponseHandlers.objectList(Post.class, JSON_LIST_PARSER);

  void getAsserts(RestResponse<Post> response) {
    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be an error code", response.isErrorResponse());

    assertPost(response.data);
  }

  private void assertPost(Post post) {
    assertNotNull("Shouldn't be null", post);
    assertNotNull("Shouldn't be null", post.getId());
    assertNotNull("Shouldn't be null", post.getUserId());
    assertNotNull("Shouldn't be null", post.getBody());
    assertNotNull("Shouldn't be null", post.getTitle());
  }
  void getListAsserts(RestResponse<List<Post>> response) {
    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be an error code", response.isErrorResponse());

    for (Post post : response.data) {
      assertPost(post);
    }
  }

  void getListAssertsWithUserId(RestResponse<List<Post>> response, int userId) {
    getListAsserts(response);
    for (Post post : response.data) {
      assertEquals("Should be equal", userId, (int) post.getUserId());
    }
  }

  void putAsserts(RestResponse<Post> response, Post post) {
    assertFalse("Shouldn't be an error code", response.isErrorResponse());
    assertTrue("Should be successful", response.isSuccessful());
    assertEquals("Should be equal", post, response.data);
  }

  void postAsserts(RestResponse<Post> response, Post post) {
    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be null", response.isErrorResponse());
    Post responseData = response.data;
    assertEquals("Should be equal", post.getUserId(), responseData.getUserId());
    assertEquals("Should be equal", post.getTitle(), responseData.getTitle());
    assertEquals("Should be equal", post.getBody(), responseData.getBody());
  }

  void deleteAsserts(RestResponse<Post> response) {
    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be an error code", response.isErrorResponse());
  }

  protected static class Post {
    private Integer id;
    private Integer userId;
    private String title;
    private String body;

    public Post(Integer id, Integer userId, String title, String body) {
      this.id = id;
      this.userId = userId;
      this.title = title;
      this.body = body;
    }

    public Integer getId() {
      return id;
    }

    public Integer getUserId() {
      return userId;
    }

    public String getTitle() {
      return title;
    }

    public String getBody() {
      return body;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Post post = (Post) o;
      return Objects.equals(id, post.id) &&
        Objects.equals(userId, post.userId) &&
        Objects.equals(title, post.title) &&
        Objects.equals(body, post.body);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }

    @Override
    public String toString() {
      return "Post{" +
        "id=" + id +
        ", userId=" + userId +
        ", title='" + title + '\'' +
        ", body='" + body + '\'' +
        '}';
    }
  }
}
