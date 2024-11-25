package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;

public class VoteStartResponse implements Response {
    private final String action = "BROADCAST";
    private final String type = "VOTE_START_RESPONSE";
    private String roomCode;

    public VoteStartResponse(String roomCode) {
        this.roomCode = roomCode;
    }
    public String getRoomCode() {
        return roomCode;
    }
    @Override
    public String getAction() {
        return action;
    }
    @Override
    public String getType() {
        return type;
    }
}
