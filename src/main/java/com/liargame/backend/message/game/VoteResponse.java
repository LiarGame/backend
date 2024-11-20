package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;

public class VoteResponse implements Response {
    private final String action = "UNICAST";
    private final String type = "VOTE_RESPONSE";
    private String playerName;
    private String suspect;
    private String roomCode;

    public VoteResponse(String playerName, String suspect, String roomCode) {
        this.playerName = playerName;
        this.suspect = suspect;
        this.roomCode = roomCode;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getSuspect() {
        return suspect;
    }

    public String getRoomCode() {
        return roomCode;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getAction() {
        return null;
    }
}
