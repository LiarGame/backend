package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;

public class VoteResult implements Response {
    private final String action = "BROADCAST";
    private final String type = "VOTE_RESULT";
    private boolean liarCaught;
    private String liarName;
    private String roomCode;

    public VoteResult(boolean liarCaught, String liarName, String roomCode) {
        this.liarCaught = liarCaught;
        this.liarName = liarName;
        this.roomCode = roomCode;
    }

    public boolean isLiarCaught() {
        return liarCaught;
    }

    public String getLiarName() {
        return liarName;
    }

    public String getRoomCode() {
        return roomCode;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getAction() {
        return action;
    }
}
