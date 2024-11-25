package com.liargame.backend.message.game;

import com.liargame.backend.message.Response;
import com.liargame.backend.tcpserver.TopicEnum;

import java.util.List;

public class GameResultResponse implements Response {
    private final String action = "BROADCAST";
    private final String type = "GAME_RESULT";
    private List<String> winner;
    private String liarName;
    private TopicEnum topic;
    private String word;
    private String roomCode;

    public GameResultResponse(List<String> winner, String liarName, TopicEnum topic, String word, String roomCode) {
        this.winner = winner;
        this.liarName = liarName;
        this.topic = topic;
        this.word = word;
        this.roomCode = roomCode;
    }

    public List<String> getWinner() {
        return winner;
    }

    public String getLiarName() {
        return liarName;
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

    @Override
    public String getAction() {
        return action;
    }
}
