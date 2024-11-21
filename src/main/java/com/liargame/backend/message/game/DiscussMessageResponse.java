package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;

public class DiscussMessageResponse implements Response {
    private final String action = "BROADCAST";
    private final String type = "DISCUSS_MESSAGE_RESPONSE";
    private String playerName;
    private String message;
    private String roomCode;

    public DiscussMessageResponse(String playerName, String message, String roomCode) {
        this.playerName = playerName;
        this.message = message;
        this.roomCode = roomCode;
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

    @Override
    public String getAction() {
        return null;
    }
}
