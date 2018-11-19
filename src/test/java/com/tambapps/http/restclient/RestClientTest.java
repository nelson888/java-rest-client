package com.tambapps.http.restclient;

import com.google.gson.Gson;
import com.tambapps.http.restclient.data.Post;
import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.request.handler.output.BodyHandlers;
import com.tambapps.http.restclient.request.handler.response.ResponseHandler;
import com.tambapps.http.restclient.request.handler.response.ResponseHandlers;
import com.tambapps.http.restclient.response.RestResponse;
import com.tambapps.http.restclient.util.ObjectParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RestClientTest {

  private static final String API_URL = "https://jsonplaceholder.typicode.com/";
  private static final int TIMEOUT = 8;
  private static final Gson GSON = new Gson();
  private static final ObjectParser JSON_PARSER = new ObjectParser() {
    @Override
    public <T> T parse(Class<T> clazz, String data) {
      return GSON.fromJson(data, clazz);
    }
  };
  private static final ResponseHandler<Post> RESPONSE_HANDLER =
      ResponseHandlers.objectHandler(Post.class, JSON_PARSER);

  private final RestClient client = new RestClient(API_URL, 1);
  private CountDownLatch latch; //allows to wait until async code is executed

  @Before
  public void init() {
    latch = new CountDownLatch(1);
  }

  @Test
  public void getTest() throws InterruptedException {
    RestRequest request = RestRequest.builder("posts/1")
        .GET()
        .build();
    client.executeAsync(request, RESPONSE_HANDLER, new RestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        getAsserts(response);
        latch.countDown();
      }
    });

    RestResponse<Post, ?> response = client.execute(request, RESPONSE_HANDLER);
    getAsserts(response);
    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  private void getAsserts(RestResponse<Post, ?> response) {
    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be an error code", response.isErrorResponse());

    Post post = response.getSuccessData();
    assertNotNull("Shouldn't be null", post);
    assertNotNull("Shouldn't be null", post.getId());
    assertNotNull("Shouldn't be null", post.getUserId());
    assertNotNull("Shouldn't be null", post.getBody());
    assertNotNull("Shouldn't be null", post.getTitle());
  }

  @Test
  public void putTest() throws InterruptedException {
    int id = 8;
    final Post post = new Post(id, 43, "title", "body");
    RestRequest request = RestRequest.builder("posts/" + id)
        .PUT()
        .output(BodyHandlers.json(GSON.toJson(post)))
        .build();

    client.executeAsync(request, RESPONSE_HANDLER, new RestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        putAsserts(response, post);
        latch.countDown();
      }
    });

    RestResponse<Post, ?> response = client.execute(request, RESPONSE_HANDLER);
    putAsserts(response, post);
    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  private void putAsserts(RestResponse<Post, ?> response, Post post) {
    assertFalse("Shouldn't be an error code", response.isErrorResponse());
    assertTrue("Should be successful", response.isSuccessful());
    assertEquals("Should be equal", post, response.getSuccessData());
  }

  @Test
  public void postTest() throws InterruptedException {
    final Post post = new Post(0, 43, "title", "body");
    RestRequest request = RestRequest.builder("posts/")
        .POST()
        .output(BodyHandlers.json(GSON.toJson(post)))
        .build();

    client.executeAsync(request, RESPONSE_HANDLER, new RestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        postAsserts(response, post);
        latch.countDown();
      }
    });

    RestResponse<Post, ?> response = client.execute(request, RESPONSE_HANDLER);
    postAsserts(response, post);
    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  private void postAsserts(RestResponse<Post, ?> response, Post post) {
    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be null", response.isErrorResponse());
    Post responseData = response.getSuccessData();
    assertEquals("Should be equal", post.getUserId(), responseData.getUserId());
    assertEquals("Should be equal", post.getTitle(), responseData.getTitle());
    assertEquals("Should be equal", post.getBody(), responseData.getBody());
  }

  @Test
  public void deleteTest() throws InterruptedException {
    RestRequest request = RestRequest.builder("posts/2")
        .DELETE()
        .build();
    client.executeAsync(request, RESPONSE_HANDLER, new RestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        deleteAsserts(response);
        latch.countDown();
      }
    });
    RestResponse<Post, ?> response = client.execute(RestRequest.builder("posts/1")
        .DELETE()
        .build(), RESPONSE_HANDLER);
    deleteAsserts(response);
    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  private void deleteAsserts(RestResponse<Post, ?> response) {
    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be an error code", response.isErrorResponse());
  }

  @Test
  public void deleteNotFoundTest() throws InterruptedException {
    RestRequest request = RestRequest.builder("posts/1456846854665")
        .DELETE()
        .build();
    client.executeAsync(request, RESPONSE_HANDLER, new RestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        deleteNotFoundAsserts(response);
        latch.countDown();
      }
    });
    RestResponse<Post, ?> response = client.execute(request, RESPONSE_HANDLER);

    deleteNotFoundAsserts(response);
    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  private void deleteNotFoundAsserts(RestResponse<Post, ?> response) {
    assertTrue("Shouldn't be successful", response.isSuccessful());
    assertTrue("Should be an error code", response.isErrorResponse());
    assertEquals("Should be equal", 404, response.getResponseCode());
  }

}
