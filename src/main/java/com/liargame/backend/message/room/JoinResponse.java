package com.liargame.backend.message.room;

import com.liargame.backend.message.Response;

import java.util.List;

public class JoinResponse implements Response {

    private final String action = "BROADCAST";
    private final String type = "JOIN_RESPONSE";
    private List<String> playerList;
    private String playerName;
    private String roomCode;

    public JoinResponse(List<String> playerList, String playerName, String roomCode) {
        this.playerList = playerList;
        this.playerName = playerName;
        this.roomCode = roomCode;
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

    @Override
    public String getAction() {
        return action;
    }
}