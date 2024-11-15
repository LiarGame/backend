package com.liargame.backend.message.room;

import com.liargame.backend.message.Message;

import java.util.List;

public class JoinResponse implements Message {

    private final String action = "BROADCAST";
    private final String type = "JOIN_RESPONSE";
    private List<String> playerList;
    private String roomCode;

    public JoinResponse(List<String> playerList, String roomCode) {
        this.playerList = playerList;
        this.roomCode = roomCode;
    }

    @Override
    public String getType() {
        return type;
    }
}
