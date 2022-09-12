package com.chat.socket.commoms.response;

import com.chat.socket.commoms.enums.StatusCode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class UserOnlineResponse extends Response {
    private List<String> userIds;

    @Builder
    public UserOnlineResponse(List<String> userIds, StatusCode statusCode) {
        this.userIds = userIds;
        this.statusCode = statusCode;
    }
}
