package com.liargame.backend.message.room;

import com.liargame.backend.message.Message;

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
