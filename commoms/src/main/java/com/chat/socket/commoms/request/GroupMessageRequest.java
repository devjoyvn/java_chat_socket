package com.chat.socket.commoms.request;

import com.chat.socket.commoms.enums.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class GroupMessageRequest extends Request {
    private String message;
    private List<String> uids;

    @Builder
    public GroupMessageRequest(@NonNull Action action, String message, List<String> uids) {
        super(action);
        this.uids = uids;
        this.message = message;
    }
}
