package org.example;

import java.io.BufferedOutputStream;

public class ServerStart {
    public static void main(String[] args) {
        final var server = new Server();

        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
            }
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
            }
        });

        server.listen();
    }
}
