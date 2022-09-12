package com.chat.socket.commoms.request;

import com.chat.socket.commoms.enums.Action;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Request implements Serializable {
    @NonNull
    protected Action action;
}
