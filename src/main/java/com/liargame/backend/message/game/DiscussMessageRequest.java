package com.liargame.backend.message.game;

import com.liargame.backend.message.Message;

public class DiscussMessageRequest implements Message {
    private final String type = "DISCUSS_MESSAGE_REQUEST";
    private String playerName;
    private String message;
    private String roomCode;

    public DiscussMessageRequest(String playerName, String message, String roomCode) {
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
}
