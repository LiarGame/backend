package com.liargame.backend.message;

public class CreateRoomRequest implements Message {
    private final String type = "CREATE_ROOM_REQUEST";
    private String playerName;

    public CreateRoomRequest(String playerName) {
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
