package com.liargame.backend.message.game;

import com.liargame.backend.message.Message;

public class VoteRequest implements Message {
    private final String type = "VOTE_REQUEST";
    private String voter;
    private String suspect;
    private String roomCode;

    public VoteRequest(String voter, String suspect, String roomCode) {
        this.voter = voter;
        this.suspect = suspect;
        this.roomCode = roomCode;
    }

    public String getVoter() {
        return voter;
    }

    public String getSuspect() {
        return suspect;
    }

    public String getRoomCode() {
        return roomCode;
    }

    @Override
    public String getType() {
        return type;
    }
}
