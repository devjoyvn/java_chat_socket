package com.chat.socket.commoms.request;

import com.chat.socket.commoms.enums.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class InformationRequest extends Request {

    @Builder
    public InformationRequest(@NonNull Action action) {
        super(action);
    }
}
