package com.liargame.backend.tcpserver;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameRoom {
    private static final Logger logger = LoggerFactory.getLogger(GameRoom.class);
    private final String roomCode;
    private final List<String> players;
    private final GameController gameController;
    private int speakCount;
    private final Set<String> votedPlayer;
    private String liar;
    private TopicEnum topic;
    private String word;

    public GameRoom(String roomCode) {
        this.roomCode = roomCode;
        this.players = new ArrayList<>();
        this.votedPlayer = new HashSet<>();
        this.gameController = new GameController(this);
    }

    public void addPlayer(String playerName) {
        logger.info("플레이어가 방에 참여했습니다: playerName={}", playerName);
        synchronized (players) {
            players.add(playerName + "(0)");
        }
    }

    public synchronized void setGameDetails(String liar, TopicEnum topic, String word) {
        this.liar = liar;
        this.topic = topic;
        this.word = word;
    }

    public synchronized String getLiar() {
        return liar;
    }

    public synchronized void setLiar(String liarWithScore) {
        this.liar = liarWithScore;
        logger.info("라이어가 재설정되었습니다: {}", liarWithScore);
    }

    public synchronized TopicEnum getTopic() {
        return topic;
    }

    public synchronized String getWord() {
        return word;
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
        synchronized (votedPlayer) {
            return votedPlayer.add(playerName);
        }
    }

    public synchronized void resetGameRoomState() {
        votedPlayer.clear();
        speakCount = 0;
        liar = null;
        topic = null;
        word = null;
    }

    public synchronized void updatePlayerScore(String playerWithScore, int additionalScore) {
        for (int i = 0; i < players.size(); i++) {
            String currentPlayer = players.get(i);
            if (currentPlayer.equals(playerWithScore)) {
                String playerName = currentPlayer.substring(0, currentPlayer.indexOf("("));
                int currentScore = Integer.parseInt(currentPlayer.substring(currentPlayer.indexOf("(") + 1, currentPlayer.indexOf(")")));
                String updatedPlayer = playerName + "(" + (currentScore + additionalScore) + ")";
                players.set(i, updatedPlayer);

                if (playerWithScore.equals(liar)) {
                    setLiar(updatedPlayer);
                }
                return;
            }
        }
    }

    public int getPlayerScore(String playerWithScore) {
        return Integer.parseInt(playerWithScore.substring(playerWithScore.indexOf("(") + 1, playerWithScore.indexOf(")")));
    }
}