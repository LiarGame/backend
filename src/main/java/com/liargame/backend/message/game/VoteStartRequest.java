package com.liargame.backend.message.game;

import com.liargame.backend.message.Message;

public class VoteStartRequest implements Message {
    private final String type = "VOTE_START_REQUEST";
    private String playerName;
    private String roomCode;

    public VoteStartRequest(String playerName, String roomCode) {
        this.playerName = playerName;
        this.roomCode = roomCode;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRoomCode() {
        return roomCode;
    }

    @Override
    public String getType() {
        return null;
    }
}
