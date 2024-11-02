package com.liargame.backend.message;

public class RoomCreateRequest implements Message {
    private final String type = "ROOM_CREATE_REQUEST";
    private String playerName;

    public RoomCreateRequest(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public String getType() {
        return type;
    }
}
