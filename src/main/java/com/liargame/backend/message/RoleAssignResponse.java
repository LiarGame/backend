package com.liargame.backend.message;

import com.liargame.backend.tcpserver.TopicEnum;

public class RoleAssignResponse implements Message {
    private final String action = "BROADCAST";
    private final String type = "ROLE_ASSIGN_RESPONSE";
    private String liar;
    private TopicEnum topic;
    private String word;
    private String roomCode;

    public RoleAssignResponse(String liar, TopicEnum topic, String word, String roomCode) {
        this.liar = liar;
        this.topic = topic;
        this.word = word;
        this.roomCode = roomCode;
    }

    public String getAction() {
        return action;
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
