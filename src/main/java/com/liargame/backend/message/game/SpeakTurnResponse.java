package com.liargame.backend.message.game;

import com.liargame.backend.message.Message;

public class SpeakTurnResponse implements Message {
    private final String action = "BROADCAST";
    private final String type = "SPEAK_TURN_RESPONSE";
    private String playerName;
    private String message;
    private String roomCode;

    public SpeakTurnResponse(String playerName, String message, String roomCode) {
        this.playerName = playerName;
        this.message = message;
        this.roomCode = roomCode;
    }

    public String getAction() {
        return action;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getMessage() {
        return message;
    }

    public String getRoomCode() {
        return roomCode;
    }

    @Override
    public String getType() {
        return null;
    }
}
