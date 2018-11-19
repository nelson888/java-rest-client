package com.tambapps.http.restclient;

import com.google.gson.Gson;
import com.tambapps.http.restclient.data.Post;
import com.tambapps.http.restclient.request.RestRequest;
import com.tambapps.http.restclient.request.handler.response.ResponseHandlers;
import com.tambapps.http.restclient.response.RestResponse;
import com.tambapps.http.restclient.util.ObjectParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RestClientTest {

  private static final String API_URL = "https://jsonplaceholder.typicode.com/";
  private static final ObjectParser JSON_PARSER = new ObjectParser() {
    final Gson gson = new Gson();
    @Override
    public <T> T parse(Class<T> clazz, String data) {
      return gson.fromJson(data, clazz);
    }
  };

  private final RestClient client = new RestClient(API_URL, 1);


  @Test
  public void getTest() {
    RestRequest request = RestRequest.builder()
        .GET()
        .endpoint("posts/1")
        .build();
    RestResponse<Post, ?> response = client.execute(request,
        ResponseHandlers.objectHandler(Post.class, JSON_PARSER));

    assertTrue("Should be successful", response.isSuccessful());

    Post post = response.getSuccessData();
    assertNotNull("Shouldn't be null", post);
    assertNotNull("Shouldn't be null", post.getId());
    assertNotNull("Shouldn't be null", post.getUserId());
    assertNotNull("Shouldn't be null", post.getBody());
    assertNotNull("Shouldn't be null", post.getTitle());
  }


}
