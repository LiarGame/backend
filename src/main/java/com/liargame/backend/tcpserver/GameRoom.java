package com.liargame.backend.tcpserver;


import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    private final String roomCode;
    private final String hostPlayerName;
    private final List<String> players;

    public GameRoom(String roomCode, String hostPlayerName) {
        this.roomCode = roomCode;
        this.hostPlayerName = hostPlayerName;
        this.players = new ArrayList<>();
        this.players.add(hostPlayerName);
    }

    public void addPlayer(String playerName) {
        players.add(playerName);
    }

    public List<String> getPlayers() {
        return players;
    }
}