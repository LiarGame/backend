package com.liargame.backend.message.room;

import com.liargame.backend.message.Response;

import java.util.List;

public class CreateRoomResponse implements Response {
    private final String action = "UNICAST";
    private final String type = "CREATE_ROOM_RESPONSE";
    private List<String> playerList;
    private String playerName;
    private String roomCode;

    public CreateRoomResponse(List<String> playerList, String playerName, String roomCode) {
        this.playerList = playerList;
        this.playerName = playerName;
        this.roomCode = roomCode;
    }

    public String getAction() {
        return action;
    }

    public List<String> getPlayerList() {
        return playerList;
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
