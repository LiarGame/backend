package com.liargame.backend.message;

public class CreateRoomResponse implements Response {
    private final String action = "UNICAST";
    private final String type = "CREATE_ROOM_RESPONSE";
    private String playerName;
    private String roomCode;

    public CreateRoomResponse(String playerName, String roomCode) {
        this.playerName = playerName;
        this.roomCode = roomCode;
    }

    public String getAction() {
        return action;
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
