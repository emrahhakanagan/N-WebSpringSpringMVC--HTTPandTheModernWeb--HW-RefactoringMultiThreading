package org.example;

public class ServerStart {
    public static void main(String[] args) {
        Server server = new Server();

        server.addHandler("GET", "/messages", (request, responseStream) -> {
            // Обработка GET запроса на "/messages"
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            // Обработка POST запроса на "/messages"
        });

        server.listen();
    }
}
