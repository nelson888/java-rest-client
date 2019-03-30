package com.tambapps.http.restclient;

import static org.junit.Assert.assertTrue;

import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.request.handler.output.BodyHandlers;
import com.tambapps.http.restclient.response.RestResponse;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsyncRestClientTest extends AbstractRestClientTest {

  private final AsyncRestClient client = new AsyncRestClient(API_URL);
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
    client.execute(request, RESPONSE_HANDLER, new AsyncRestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        getAsserts(response);
        latch.countDown();
      }
    });

    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  @Test
  public void putTest() throws InterruptedException {
    int id = 8;
    final Post post = new Post(id, 43, "title", "body");
    RestRequest request = RestRequest.builder("posts/" + id)
        .PUT()
        .output(BodyHandlers.json(GSON.toJson(post)))
        .build();

    client.execute(request, RESPONSE_HANDLER, new AsyncRestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        putAsserts(response, post);
        latch.countDown();
      }
    });
    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  @Test
  public void postTest() throws InterruptedException {
    final Post post = new Post(0, 43, "title", "body");
    RestRequest request = RestRequest.builder("posts/")
        .POST()
        .output(BodyHandlers.json(GSON.toJson(post)))
        .build();

    client.execute(request, RESPONSE_HANDLER, new AsyncRestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        postAsserts(response, post);
        latch.countDown();
      }
    });

    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  @Test
  public void deleteTest() throws InterruptedException {
    RestRequest request = RestRequest.builder("posts/1")
        .DELETE()
        .build();
    client.execute(request, RESPONSE_HANDLER, new AsyncRestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        deleteAsserts(response);
        latch.countDown();
      }
    });
    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  @Test
  public void getListTest() throws InterruptedException {
    RestRequest request = RestRequest.builder("/posts")
      .GET()
      .build();
    client.execute(request, LIST_RESPONSE_HANDLER, new AsyncRestClient.Callback<List<Post>, List<Post>>() {
      @Override
      public void call(RestResponse<List<Post>, List<Post>> response) {
        getListAsserts(response);
        latch.countDown();
      }
    });

    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

  @Test
  public void getListWithParameterTest() throws InterruptedException {
    final int userId = 1;
    RestRequest request = RestRequest.builder("/posts")
      .parameter("userId", userId)
      .GET()
      .build();
    client.execute(request, LIST_RESPONSE_HANDLER, new AsyncRestClient.Callback<List<Post>, List<Post>>() {
      @Override
      public void call(RestResponse<List<Post>, List<Post>> response) {
        getListAssertsWithUserId(response, userId);
        latch.countDown();
      }
    });

    assertTrue("Should be true", latch.await(TIMEOUT, TimeUnit.SECONDS));
  }

}
