# Java REST client

This project is a Java library for working with REST services. It provides
a client that can send requests to a REST server. It was written in Java 7 

## Architecture

### RestClient
It is the main class of this library, that will send your requests synchronously/asynchronously. 

### RestRequest
The RestRequest holds the data of an Http request to a REST service.
You can construct a request with a RestRequest.Builder.

### BodyHandler
The BodyHandler interface is used for writing content into request, such as objects converted to
json, files or anything else. Some are implemented in BodyHandlers, and if you want to implement your own,
you can extend AbstractBodyHandler

### ResponseHandler
The ResponseHandler interface handles the response given by the REST server. It converts the input stream
of the URLConnection into a given type. Some of them are implemented in ResponseHandlers.
Feel free to implement your own by implementing ResponseHandler

### Examples
Send a request synchronously:
```java
RestClient client = new RestClient(API_URL);
RestRequest request = RestRequest.builder("posts/1")
        .GET()
        .build();
RestResponse<Post, ?> response = client.execute(request, RESPONSE_HANDLER);
if (response.isSuccessful()) {
  handlePost(response.getSuccessData());
} else if (response.isErrorResponse()) {
  handleError(response.getErrorData());
} else { // has exception
  handleException(response.getException());
}
```

Or asynchronously:
```java
RestRequest request = RestRequest.builder("posts/" + id)
        .PUT()
        .output(BodyHandlers.json(gson.toJson(post)))
        .build();
client.executeAsync(request, RESPONSE_HANDLER, new RestClient.Callback<Post, Post>() {
      @Override
      public void call(RestResponse<Post, Post> response) {
        if (response.isSuccessful()) {
          handlePost(response.getSuccessData());
        }
      }
    });

```

Send multipart files:
```java
RestRequest request = RestRequest.builder(FILE_STORAGE_ENDPOINT)
        .POST()
        .output(BodyHandlers.multipartFile(file))
        .build();
        
    client.executeAsync(request, ResponseHandlers.stringHandler(),
        new RestClient.Callback<String, String>() {
              @Override
              public void call(RestResponse<Post, Post> response) {
                print(response.getData());
              }
            });
```
Receive a multipart file:
```java
File file = new File("path/to/file/to/write");
RestRequest request = RestRequest.builder(FILE_STORAGE_ENDPOINT + fileId)
        .GET()
        .build();
client.executeAsync(request, ResponseHandlers.multipartFileHandler(file),
        ResponseHandlers.stringHandler(),
        new RestClient.Callback<File, String>() {
              @Override
              public void call(RestResponse<File, String> response) {
                if (response.isSuccessful()) {
                  print("File saved successfully");
                } else {
                  print("An error occured");
                }
              }
            });
```
