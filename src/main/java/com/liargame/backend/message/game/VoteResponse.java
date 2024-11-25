package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;

import java.util.List;

public class VoteResponse implements Response {
    private final String action = "UNICAST";
    private final String type = "VOTE_RESPONSE";
    private List<String> playerList;
    private String playerName;
    private String suspect;
    private String roomCode;

    public VoteResponse(List<String> playerList, String playerName, String suspect, String roomCode) {
        this.playerList = playerList;
        this.playerName = playerName;
        this.suspect = suspect;
        this.roomCode = roomCode;
    }

    public List<String> getPlayerList() {
        return playerList;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getSuspect() {
        return suspect;
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
