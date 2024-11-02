package com.liargame.backend.message;

public class StartGameRequest implements Message {
    private final String type = "START_GAME_REQUEST";
    private String playerName;
    private String roomCode;

    public StartGameRequest(String playerName, String roomCode) {
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
