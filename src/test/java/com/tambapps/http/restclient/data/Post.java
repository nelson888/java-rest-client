package com.tambapps.http.restclient.data;

import java.util.Objects;

public class Post {

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

  @Override public String toString() {
    return "Post{" +
        "id=" + id +
        ", userId=" + userId +
        ", title='" + title + '\'' +
        ", body='" + body + '\'' +
        '}';
  }
}
