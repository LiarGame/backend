package com.liargame.backend.message.game;


import com.liargame.backend.message.Message;

public class GuessWordRequest implements Message {
    private final String type = "GUESS_WORD_REQUEST";
    private String playerName;
    private String roomCode;
    private String guessWord;

    public GuessWordRequest(String playerName, String roomCode, String guessWord) {
        this.playerName = playerName;
        this.roomCode = roomCode;
        this.guessWord = guessWord;
    }
    public String getGuessWord(){return guessWord;}
    public String getPlayerName() {
        return playerName;
    }

    public String getRoomCode() {
        return roomCode;
    }

    @Override
    public String getType() {
        return null;
    }
}

