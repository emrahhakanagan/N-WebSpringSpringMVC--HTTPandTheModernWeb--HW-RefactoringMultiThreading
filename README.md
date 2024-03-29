## --- NETOLOGY HOMEWORK ---
### WEB, SPRING & SPRING MVC --> HTTP and The Modern Web


#### --> Task 1 --> Server


# Enhanced Java HTTP Server

This project presents an enhanced version of a Java HTTP server, now supporting `x-www-form-urlencoded` request body parsing. This feature is essential for handling form submissions, making the server suitable for more complex web applications.

## Features
- Support for `GET` and `POST` HTTP methods.
- Customizable request handlers for different paths.
- Parsing and handling of `x-www-form-urlencoded` request bodies, enabling the server to process form data sent via POST requests.

## Usage
To add a new handler, use the `addHandler` method specifying the HTTP method, path, and the handling logic.

Example:
```java
server.addHandler("POST", "/submit-form", (request, responseStream) -> {
    // Handling code here
});
