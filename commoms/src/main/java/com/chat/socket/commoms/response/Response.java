package com.chat.socket.commoms.response;

import com.chat.socket.commoms.enums.StatusCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class Response implements Serializable {
    protected StatusCode statusCode;
}
