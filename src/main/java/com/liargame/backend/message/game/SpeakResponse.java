package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;

import java.util.List;

public class SpeakResponse implements Response {
    private final String action = "BROADCAST";
    private final String type = "SPEAK_RESPONSE";
    private List<String> playerList;
    private String speakingPlayer;
    private String message;
    private String nextPlayer;
    private String roomCode;
    private Boolean startDiscussFlag;

    public SpeakResponse(List<String> playerList, String speakingPlayer, String message, String nextPlayer, String roomCode, Boolean startDiscussFlag) {
        this.playerList = playerList;
        this.speakingPlayer = speakingPlayer;
        this.message = message;
        this.nextPlayer = nextPlayer;
        this.roomCode = roomCode;
        this.startDiscussFlag = startDiscussFlag;
    }

    public String getAction() {
        return action;
    }

    public List<String> getPlayerList() {
        return playerList;
    }

    public String getSpeakingPlayer() {
        return speakingPlayer;
    }

    public String getMessage() {
        return message;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public Boolean getStartDiscussFlag() {
        return startDiscussFlag;
    }

    @Override
    public String getType() {
        return type;
    }
}
