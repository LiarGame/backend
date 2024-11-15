package com.liargame.backend.message.base;

import com.liargame.backend.message.Message;

public class ErrorResponse implements Message {
    private final String action = "UNICAST";
    private final String type = "ERROR";
    private String playerName;
    private String message;

    public ErrorResponse(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }

    @Override
    public String getType() {
        return type;
    }
}
