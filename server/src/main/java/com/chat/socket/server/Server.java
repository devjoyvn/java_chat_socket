package com.chat.socket.server;

import com.chat.socket.commoms.enums.StatusCode;
import com.chat.socket.commoms.request.GroupMessageRequest;
import com.chat.socket.commoms.request.MessageRequest;
import com.chat.socket.commoms.request.Request;
import com.chat.socket.commoms.response.MessageResponse;
import com.chat.socket.commoms.response.Response;
import com.chat.socket.commoms.response.UserOnlineResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Server {

    private ServerSocket serverSocket;
    private Map<String, ClientHandler> clientHandlers;

    public Server() {
        this.clientHandlers = new HashMap<>();
    }

    public void start(int port) {
        System.out.println("Server starting!!!");
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(serverSocket.getInetAddress().getHostName());
            System.out.println(serverSocket.getLocalPort());
            while (true) {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
                clientHandler.start();
                this.clientHandlers.put(clientHandler.getUid(), clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getUserIdOnline() {
        return this.clientHandlers.values().stream()
                .map(ClientHandler::getUid)
                .collect(Collectors.toList());
    }


    @Getter
    @Setter
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String uid;

        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            this.uid = UUID.randomUUID().toString();
        }

        private void response(Response response) throws IOException {
            this.out.writeObject(response);
            this.out.flush();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Object input = in.readObject();
                    if (ObjectUtils.isNotEmpty(input)) {
                        Request request = (Request) input;
                        switch (request.getAction()) {
                            case GET_USERS_ONLINE: {
                                this.response(UserOnlineResponse.builder()
                                        .userIds(getUserIdOnline())
                                        .statusCode(StatusCode.OK)
                                        .build());
                                break;
                            }
                            case SEND_MESSAGE_TO_USER_SPECIFIC: {
                                ClientHandler clientHandler = clientHandlers.get(((MessageRequest) (request)).getUid());
                                if (clientHandler == null) {
                                    this.response(MessageResponse.builder()
                                            .statusCode(StatusCode.BAD_REQUEST)
                                            .build());
                                } else {
                                    clientHandler.response(MessageResponse.builder()
                                            .message(((MessageRequest) (request)).getMessage())
                                            .senderId(this.getUid())
                                            .statusCode(StatusCode.OK)
                                            .build());
                                }
                                break;
                            }
                            case CHAT_ALL: {
                                GroupMessageRequest groupMessageRequest = (GroupMessageRequest) request;
                                for (String s : groupMessageRequest.getUids()) {
                                    ClientHandler clientHandler = clientHandlers.get(s);
                                    if (clientHandler != null) {
                                        clientHandler.response(MessageResponse.builder()
                                                .senderId(this.getUid())
                                                .message(groupMessageRequest.getMessage())
                                                .statusCode(StatusCode.OK)
                                                .build());
                                    }
                                }
                                break;
                            }
                            case DISCONNECT: {
                                clientHandlers.remove(this.getUid());
                                break;
                            }
                            default:
                                break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }

                    if (out != null) {
                        out.close();
                    }

                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
