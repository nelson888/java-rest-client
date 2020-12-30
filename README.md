# Java REST client
It is actually written in Kotlin

This project is a Kotlin library for working with REST services. It provides
a client that can send requests to a REST server. It was written in Kotlin.
The architecture of this client is inspired from the Java 9 Http Client
There are multiple clients. Each have the same API, but they are backed by different library:
- `http-client-url` is backed by native Java URL class
- `http-client-okhttp3` is backed by okhttp3

## Architecture

### RestClient
It is the main class of this library, that will send your requests synchronously/asynchronously.
You can also set an authentication (basic or jwt) 

### RestRequest
The RestRequest holds the data of an Http request to a REST service.
You can construct a request with a RestRequest.Builder.

### BodyProcessor
The BodyProcessor interface is used for writing content into request, such as objects converted to
json, files or anything else. Some are implemented in BodyProcessors, and if you want to implement your own,
you can extend AbstractBodyProcessor

### ResponseHandler
The ResponseHandler interface handles the response given by the REST server. It converts the input stream
of the URLConnection into a given type. Some of them are implemented in ResponseHandlers.
Feel free to implement your own by implementing ResponseHandler

### RestResponse
It is the representation of a response from a REST service.

### ObjectParser
This interface is used to parse data from a RestResponse and convert it into 
a given class. It is useful to convert a response body into an object when using it with 
`ResponseHandlers.objectHandler(...)` or `ResponseHandlers.objectListHandler(...)`. For example, it could be a JSON, or XML parser.


### ObjectConverter
The ObjectConverter is an ObjectParser that can also `stringify` objects, meaning
that it can for example convert an object into json to put it an a request body
with `BodyProcessors.json(...)`.

## Examples
Send a request synchronously:
```java
RestClient client = new RestClient(API_URL);
RestRequest request = RestRequest.builder("posts/1")
        .GET()
        .build();
final ResponseHandler<Post> responseHandler =
    ResponseHandlers.objectHandler(Post.class, JSON_PARSER);
final ResponseHandler<Post> errorResponseHandler =
    ResponseHandlers.objectHandler(ErrorResponse.class, JSON_PARSER);
RestResponse<Post, ErrorResponse> response = client.execute(request, responseHandler, errorResponseHandler);
if (response.isSuccessful()) {
  handlePost(response.getData());
} else if (response.isErrorResponse()) {
  handleError(response.getErrorData(ResponseHandlers.stringHandler()));
}
```

Or asynchronously:
```java
RestClient client = new AsyncRestClient(API_URL);
RestRequest request = RestRequest.builder("posts/" + id)
        .PUT()
        .body(BodyProcessors.json(objectConverter.stringify(post)))
        .build();
client.execute(request, 
    RESPONSE_HANDLER, 
    new RestClient.Callback<Post>() {
      @Override
      public void call(RestResponse<Post> response) {
        if (response.isSuccessful()) {
          handlePost(response.getData());
        }
      }
    });

```

Send multipart files:
```java
RestRequest request = RestRequest.builder(FILE_STORAGE_ENDPOINT)
        .POST()
        .body(BodyProcessors.multipartFile(file))
        .build();
        
client.execute(request, ResponseHandlers.stringHandler(),
        new RestClient.Callback<String>() {
              @Override
              public void call(RestResponse<String> response) {
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
client.execute(request, ResponseHandlers.multipartFileHandler(file),
        new RestClient.Callback<File>() {
              @Override
              public void call(RestResponse<File> response) {
                if (response.isSuccessful()) {
                  print("File saved successfully");
                } else {
                  print("An error occured");
                }
              }
            });
```
Receive a list of object:
```java
int userId = 2;
RestRequest request = RestRequest.builder("/posts")
    .parameter("userId", userId)
    .GET()
    .build();
final ResponseHandler<List<Post>> listResponseHandler = 
    ResponseHandlers.objectListHandler(Post.class, JSON_LIST_PARSER);
RestResponse<List<Post>> response = client.execute(request, listResponseHandler);
```