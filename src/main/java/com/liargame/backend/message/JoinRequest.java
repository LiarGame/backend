package com.liargame.backend.message;

public class JoinRequest implements Message {
    private final String type = "JOIN_REQUEST";
    private String playerName;
    private String roomCode;

    public JoinRequest(String playerName, String roomCode) {
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
        return type;
    }
}
