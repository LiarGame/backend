package com.liargame.backend.tcpserver;


import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    private final String roomCode;
    private final List<String> players;
    private final GameController gameController;


    public GameRoom(String roomCode) {
        this.roomCode = roomCode;
        this.players = new ArrayList<>();
        this.gameController = new GameController(this);
    }

    public void addPlayer(String playerName) {
        synchronized (players) {
            players.add(playerName);
        }
    }

    public List<String> getPlayers() {
        synchronized (players) {
            return players;
        }
    }

    public GameController getGameController() {
        return gameController;
    }

    public String getRoomCode() {
        return roomCode;
    }
}