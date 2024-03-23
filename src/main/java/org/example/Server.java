package org.example;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    private final Map<String, Map<String, Handler>> handlers = new HashMap<>();
    private ServerPropertiesLoading serverPropertiesLoading;
    private int port;

    public Server() {
        serverPropertiesLoading = new ServerPropertiesLoading();
        this.port = serverPropertiesLoading.getPort();
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.computeIfAbsent(method, k -> new HashMap<>()).put(path, handler);
    }

    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(() -> handleConnection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {

            Request request = parseRequest(in);
            Handler handler = findHandler(request);

            if (handler != null) {
                handler.handle(request, out);
            } else {
                sendNotFound(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Request parseRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Received an empty request");
        }

        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }
        String method = parts[0];
        String path = parts[1];

        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            int separator = line.indexOf(":");
            if (separator == -1) {
                continue;
            }
            String headerName = line.substring(0, separator).trim();
            String headerValue = line.substring(separator + 1).trim();
            headers.put(headerName, headerValue);
        }

        return new Request(method, path, headers, in);
    }

    private Handler findHandler(Request request) {
        Map<String, Handler> methodHandlers = handlers.get(request.getMethod());
        if (methodHandlers != null) {
            return methodHandlers.get(request.getPath());
        }
        return null;
    }

    private void sendNotFound(BufferedOutputStream out) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n";
        out.write(response.getBytes());
        out.flush();
    }

}
