package com.chat.socket.commoms.response;

import com.chat.socket.commoms.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MessageResponse extends Response {

    private String message;
    private String senderId;

    @Builder
    public MessageResponse(String message, String senderId, StatusCode statusCode) {
        this.message = message;
        this.senderId = senderId;
        this.statusCode = statusCode;
    }
}
