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

//    public void broadcast(String message) {
//        synchronized (players) {
//            for (PrintWriter pw : players.values()) {
//                pw.println(message);
//                pw.flush();
//            }
//        }
//    }
}