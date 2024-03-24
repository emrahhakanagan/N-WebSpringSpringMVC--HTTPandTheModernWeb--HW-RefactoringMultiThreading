package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png"
            , "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html"
            , "/events.html", "/events.js");
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



/*

    private void handleConnection(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                System.out.println("Connection closed by client or received an empty request");
                return;
            }

            String[] parts = requestLine.split(" ");
            if (parts.length != 3) {
                sendBadRequest(out);
                return;
            }

            String method = parts[0];
            String path = parts[1];
            Map<String, String> headers = new HashMap<>();
            String line;

            // Используйте in.readLine() для чтения заголовков
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                int separator = line.indexOf(":");
                if (separator == -1) {
                    continue;
                }
                String headerName = line.substring(0, separator).trim();
                String headerValue = line.substring(separator + 1).trim();
                headers.put(headerName, headerValue);
            }

            // Передаем BufferedReader в Request, чтобы можно было читать тело запроса
            Request request = new Request(method, path, headers, in);
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


    */



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

    /*

    private void sendBadRequest(BufferedOutputStream out) throws IOException {
        String response = "HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\n\r\n";
        out.write(response.getBytes());
        out.flush();
    }

*/

    private Request parseRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        System.out.println("REQUEST LINE --->>> " + requestLine);

        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Received an empty request");
        }

        String[] parts = requestLine.split(" ");
        System.out.println("PARTS --->>> " + Arrays.toString(parts));

        if (parts.length != 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        String method = parts[0];
        String path = parts[1];

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int separator = line.indexOf(":");
            if (separator == -1) {
                continue;
            }
            String headerName = line.substring(0, separator).trim();
            String headerValue = line.substring(separator + 1).trim();
            headers.put(headerName, headerValue);
        }

        return new Request(method, path, headers, reader);
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
