package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;
import com.liargame.backend.tcpserver.TopicEnum;

import java.util.List;

public class RoleAssignResponse implements Response {
    private final String action = "BROADCAST";
    private final String type = "ROLE_ASSIGN_RESPONSE";
    private List<String> playerList;
    private String liar;
    private TopicEnum topic;
    private String word;
    private String roomCode;

    public RoleAssignResponse(List<String> playerList, String liar, TopicEnum topic, String word, String roomCode) {
        this.playerList = playerList;
        this.liar = liar;
        this.topic = topic;
        this.word = word;
        this.roomCode = roomCode;
    }

    public String getAction() {
        return action;
    }

    public List<String> getPlayerList() {
        return playerList;
    }

    public String getLiar() {
        return liar;
    }

    public TopicEnum getTopic() {
        return topic;
    }

    public String getWord() {
        return word;
    }

    public String getRoomCode() {
        return roomCode;
    }


    @Override
    public String getType() {
        return type;
    }
}
