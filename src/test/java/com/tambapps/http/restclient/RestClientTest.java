package com.tambapps.http.restclient;

import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.request.handler.output.BodyHandlers;
import com.tambapps.http.restclient.response.RestResponse;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestClientTest extends AbstractRestClientTest {

  private final RestClient client = new RestClient(API_URL);

  @Test
  public void getTest() {
    RestRequest request = RestRequest.builder("posts/1")
        .GET()
        .build();

    RestResponse<Post, ?> response = client.execute(request, RESPONSE_HANDLER);
    getAsserts(response);
  }

  @Test
  public void putTest() {
    int id = 8;
    final Post post = new Post(id, 43, "title", "body");
    RestRequest request = RestRequest.builder("posts/" + id)
        .PUT()
        .output(BodyHandlers.json(GSON.toJson(post)))
        .build();

    RestResponse<Post, ?> response = client.execute(request, RESPONSE_HANDLER);
    putAsserts(response, post);
  }
  @Test
  public void postTest() {
    final Post post = new Post(0, 43, "title", "body");
    RestRequest request = RestRequest.builder("posts/")
        .POST()
        .output(BodyHandlers.json(GSON.toJson(post)))
        .build();

    RestResponse<Post, ?> response = client.execute(request, RESPONSE_HANDLER);
    postAsserts(response, post);
  }

  @Test
  public void deleteTest() {
    RestResponse<Post, ?> response = client.execute(RestRequest.builder("posts/1")
        .DELETE()
        .build(), RESPONSE_HANDLER);
    deleteAsserts(response);
  }

  @Test
  public void getListTest() {
    RestRequest request = RestRequest.builder("/posts")
      .GET()
      .build();

    RestResponse<List<Post>, ?> response = client.execute(request, LIST_RESPONSE_HANDLER);
    getListAsserts(response);
  }

  @Test
  public void getListWithParameterTest() {
    int userId = 2;
    RestRequest request = RestRequest.builder("/posts")
      .parameter("userId", userId)
      .GET()
      .build();

    RestResponse<List<Post>, ?> response = client.execute(request, LIST_RESPONSE_HANDLER);
    getListAssertsWithUserId(response, userId);
  }
}
