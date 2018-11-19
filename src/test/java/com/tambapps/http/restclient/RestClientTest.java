package com.tambapps.http.restclient;

import com.google.gson.Gson;
import com.tambapps.http.restclient.data.Post;
import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.request.handler.output.BodyHandlers;
import com.tambapps.http.restclient.request.handler.response.ResponseHandlers;
import com.tambapps.http.restclient.response.RestResponse;
import com.tambapps.http.restclient.util.ObjectParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RestClientTest {

  private static final String API_URL = "https://jsonplaceholder.typicode.com/";
  private static final Gson GSON = new Gson();
  private static final ObjectParser JSON_PARSER = new ObjectParser() {
    @Override
    public <T> T parse(Class<T> clazz, String data) {
      return GSON.fromJson(data, clazz);
    }
  };

  private final RestClient client = new RestClient(API_URL, 1);

  @Test
  public void getTest() {
    RestRequest request = RestRequest.builder("posts/1")
        .GET()
        .build();
    RestResponse<Post, ?> response = client.execute(request,
        ResponseHandlers.objectHandler(Post.class, JSON_PARSER));

    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be null", response.isErrorResponse());

    Post post = response.getSuccessData();
    assertNotNull("Shouldn't be null", post);
    assertNotNull("Shouldn't be null", post.getId());
    assertNotNull("Shouldn't be null", post.getUserId());
    assertNotNull("Shouldn't be null", post.getBody());
    assertNotNull("Shouldn't be null", post.getTitle());
  }

  @Test
  public void putTest() {
    int id = 8;
    Post post = new Post(id, 43, "title", "body");
    RestRequest request = RestRequest.builder("posts/" + id)
        .PUT()
        .output(BodyHandlers.json(GSON.toJson(post)))
        .build();

    RestResponse<Post, ?> response = client.execute(request,
        ResponseHandlers.objectHandler(Post.class, JSON_PARSER));

    assertFalse("Shouldn't be null", response.isErrorResponse());
    assertTrue("Should be successful", response.isSuccessful());
    assertEquals("Should be equal", post, response.getSuccessData());
  }

  @Test
  public void postTest() {
    Post post = new Post(0, 43, "title", "body");
    RestRequest request = RestRequest.builder("posts/")
        .POST()
        .output(BodyHandlers.json(GSON.toJson(post)))
        .build();

    RestResponse<Post, ?> response = client.execute(request,
        ResponseHandlers.objectHandler(Post.class, JSON_PARSER));

    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be null", response.isErrorResponse());

    Post responseData = response.getSuccessData();
    assertEquals("Should be equal", post.getUserId(), responseData.getUserId());
    assertEquals("Should be equal", post.getTitle(), responseData.getTitle());
    assertEquals("Should be equal", post.getBody(), responseData.getBody());
  }

  @Test
  public void deleteTest() {
    RestRequest request = RestRequest.builder("posts/1")
        .DELETE()
        .build();
    RestResponse<Post, ?> response = client.execute(request,
        ResponseHandlers.objectHandler(Post.class, JSON_PARSER));

    assertTrue("Should be successful", response.isSuccessful());
    assertFalse("Shouldn't be null", response.isErrorResponse());
  }
}
