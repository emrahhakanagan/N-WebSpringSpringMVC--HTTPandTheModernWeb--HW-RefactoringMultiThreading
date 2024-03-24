package org.example;

public class ServerStart {
    public static void main(String[] args) {
        final var server = new Server();

        server.addHandler("GET", "/messages", (request, responseStream) -> {
            // TODO: handlers code
        });
        server.addHandler("POST", "/messages", (request, responseStream) -> {
            // TODO: handlers code
        });

        server.listen();
    }
}
