package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpOutput;

import java.util.List;

public class RestartRoomResponse implements Response {
    private final String action = "BROADCAST";
    private final String type = "RESTART_ROOM_RESPONSE";
    private List<String> playerList;
    private String roomCode;

    public RestartRoomResponse(List<String> playerList, String roomCode) {
        this.playerList = playerList;
        this.roomCode = roomCode;
    }

    public List<String> getPlayerList() {
        return playerList;
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
