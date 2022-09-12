package com.chat.socket.commoms.request;

import com.chat.socket.commoms.enums.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class MessageRequest extends Request {
    private String message;
    private String uid;
    @Builder
    public MessageRequest(@NonNull Action action, String message, String uid) {
        super(action);
        this.message = message;
        this.uid = uid;
    }
}
