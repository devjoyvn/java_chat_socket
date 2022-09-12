package com.chat.socket.client;

public class Main {
    public static void main(String[] agrs) {
        Client client = new Client();
        client.startConnection("localhost", 8080);
    }
}
