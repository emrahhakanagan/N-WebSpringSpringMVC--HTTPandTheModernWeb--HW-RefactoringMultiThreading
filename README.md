## --- NETOLOGY HOMEWORK ---
### WEB, SPRING & SPRING MVC --> HTTP and The Modern Web


#### --> Task 1 --> Server


# Simple Java HTTP Server

This project represents a simple implementation of an HTTP server in Java. It's capable of handling basic HTTP requests and has been extended to support multipart/form-data requests for handling file uploads and mixed content forms.

## Features

- Basic HTTP request handling
- Dynamic content generation
- Support for query parameters (`GET` and `POST`)
- Handling multipart/form-data requests for file uploads

## Getting Started

To run the server, execute the `ServerStart` class. The server will start listening for incoming HTTP requests on the configured port.

### Handling Basic Requests

The server can handle basic `GET` and `POST` requests. Handlers can be added for specific paths using the `addHandler` method in the `Server` class.

### Handling Query Parameters

The server supports handling query parameters from both `GET` and `POST` requests. Use the `Request.getQueryParam(String name)` method to retrieve query parameters by name.

### Handling Multipart/Form-Data Requests

The server now supports multipart/form-data requests, enabling the processing of file uploads and mixed content forms. This feature is powered by the Apache Commons FileUpload library, providing efficient and flexible handling of multipart data.

#### Quick Start

To handle multipart/form-data submissions:

1. Ensure your form is set with `enctype="multipart/form-data"` and method as `POST`.
2. Use the `Request.getPart(String name)` method to retrieve specific file items or form fields by name.
3. Use the `Request.getMultipartItems()` method to access all items in the request for more complex processing needs.

Refer to the examples section for detailed usage scenarios.

## Configuration

Server properties such as port can be configured in the `settings.txt` file located in the resources directory.

## Examples

### Basic Request Handling

```java
server.addHandler("GET", "/path", (request, responseStream) -> {
    // Handle GET request for /path
});
```


### Query Parameters
```javaserver.addHandler("GET", "/search", (request, responseStream) -> {

String query = request.getQueryParam("query");
// Process query
});
```

### Multipart/Form-Data Submission

```java
server.addHandler("POST", "/upload", (request, responseStream) -> {
    FileItem fileItem = request.getPart("file");
    if (fileItem != null) {
        // Process file upload
    }
});
```

## Dependencies
Apache HttpClient for handling HTTP requests and responses.
Apache Commons FileUpload for parsing and handling multipart/form-data requests.
For a complete list of dependencies, refer to the pom.xml file.
