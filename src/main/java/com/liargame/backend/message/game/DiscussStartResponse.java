package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;

public class DiscussStartResponse implements Response {
    private final String action = "BROADCAST";
    private final String type = "DISCUSS_START_RESPONSE";
    private String roomCode;

    public DiscussStartResponse(String roomCode) {
        this.roomCode = roomCode;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public String getType() {
        return type;
    }

    public String getRoomCode() {
        return roomCode;
    }
}
