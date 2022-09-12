package com.chat.socket.client;


import com.chat.socket.commoms.enums.Action;
import com.chat.socket.commoms.enums.StatusCode;
import com.chat.socket.commoms.request.GroupMessageRequest;
import com.chat.socket.commoms.request.InformationRequest;
import com.chat.socket.commoms.request.MessageRequest;
import com.chat.socket.commoms.request.Request;
import com.chat.socket.commoms.response.MessageResponse;
import com.chat.socket.commoms.response.UserOnlineResponse;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Scanner scanner;
    List<String> userOnlines;

    private void sendRequest(Request req) throws IOException {
        this.out.writeObject(req);
        this.out.flush();
    }

    private void close() throws IOException {
        if (this.in != null) {
            this.in.close();
        }
        if (this.out != null) {
            this.out.close();
        }

        if (this.clientSocket != null) {
            this.clientSocket.close();
        }
    }

    private void getUserOnlines() throws IOException {
        sendRequest(InformationRequest.builder().action(Action.GET_USERS_ONLINE).build());
    }

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.in = new ObjectInputStream(clientSocket.getInputStream());
            scanner = new Scanner(System.in);
            userOnlines = new ArrayList<>();
            new ResponseProcess().start();
            getUserOnlines();
            while (true) {
                System.out.println("Chose your options");
                System.out.println("1: GET ALL USER ONLINE");
                System.out.println("2: SEND MESSAGE");
                System.out.println("3: SEND ALL");
                System.out.println("-1: ESC");
                String ch = scanner.next();
                switch (ch) {
                    case "1": {
                        getUserOnlines();
                        break;
                    }
                    case "2": {
                        System.out.println("User onlines: ");
                        if (this.userOnlines.isEmpty()) {
                            System.out.println("No user online");
                            break;
                        }

                        for (int i = 0; i < this.userOnlines.size(); i++) {
                            System.out.println("User " + (i + 1) + ": " + this.userOnlines.get(i));
                        }

                        System.out.println("Please select number from 1 to " + this.userOnlines.size() + " to send the message");
                        int choise = scanner.nextInt();
                        System.out.println();
                        System.out.print("Enter message: ");
                        scanner.nextLine();
                        String message = scanner.nextLine();

                        sendRequest(MessageRequest.builder()
                                .action(Action.SEND_MESSAGE_TO_USER_SPECIFIC)
                                .uid(this.userOnlines.get(choise - 1))
                                .message(message)
                                .build());
                        break;
                    }
                    case "3": {
                        System.out.print("Enter message: ");
                        scanner.nextLine();
                        String message = scanner.nextLine();
                        sendRequest(GroupMessageRequest.builder()
                                .action(Action.CHAT_ALL)
                                .message(message)
                                .uids(this.userOnlines)
                                .build());
                        break;
                    }
                    case "-1": {
                        sendRequest(InformationRequest.builder().action(Action.DISCONNECT).build());
                        close();
                        return;
                    }
                    default:
                        break;
                }
            }

        } catch (IOException e) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class ResponseProcess extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    Object object = in.readObject();
                    if (ObjectUtils.isEmpty(object)) {
                        continue;
                    }

                    if (object instanceof UserOnlineResponse) {
                        UserOnlineResponse userOnlineResponse = (UserOnlineResponse) object;
                        if (StatusCode.OK.equals(userOnlineResponse.getStatusCode())) {
                            userOnlineResponse.getUserIds().forEach(s -> System.out.println(s));
                            userOnlines = userOnlineResponse.getUserIds();
                        } else {
                            System.out.println("Request failed!!!");
                        }
                    }

                    if (object instanceof MessageResponse) {
                        MessageResponse messageResponse = (MessageResponse) object;
                        System.out.println("You is received: from " + messageResponse.getSenderId());
                        System.out.println("Message " + messageResponse.getMessage());
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
