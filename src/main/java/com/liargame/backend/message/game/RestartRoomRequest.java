package com.liargame.backend.message.game;


import com.liargame.backend.message.Message;

public class RestartRoomRequest implements Message {
    private final String type = "RESTART_ROOM_REQUEST";
    private String playerName;
    private String roomCode;

    public RestartRoomRequest(String playerName, String roomCode) {
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
