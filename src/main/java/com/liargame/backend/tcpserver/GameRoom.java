package com.liargame.backend.tcpserver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameRoom {
    private static final Logger logger = LoggerFactory.getLogger(GameRoom.class);
    private final String roomCode;
    private final List<String> players;
    private final GameController gameController;
    private int speakCount;
    private Set<String> votedPlayer;

    public GameRoom(String roomCode) {
        this.roomCode = roomCode;
        this.players = new ArrayList<>();
        this.gameController = new GameController(this);
    }

    public void addPlayer(String playerName) {
        logger.info("플레이어가 방에 참여했습니다: playerName={}", playerName);
        synchronized (players) {
            players.add(playerName);
        }
    }

    public synchronized List<String> getPlayers() {
        return new ArrayList<>(players);
    }

    public GameController getGameController() {
        return gameController;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public synchronized int getSpeakCount() {
        return speakCount;
    }

    public synchronized void incrementSpeakCount() {
        speakCount++;
    }

    public Set<String> getVotedPlayer() {
        return votedPlayer;
    }

    public boolean addVotedPlayer(String playerName) {
        return votedPlayer.add(playerName);
    }
}