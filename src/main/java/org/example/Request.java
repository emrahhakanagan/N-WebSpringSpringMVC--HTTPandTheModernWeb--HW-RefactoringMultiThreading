package org.example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final BufferedReader body;

    public Request(String method, String path, Map<String, String> headers, BufferedReader body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }


    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public BufferedReader getBody() {
        return body;
    }
}

