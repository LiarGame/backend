package com.liargame.backend.message;

public class ErrorResponse implements Response {
    private final String action = "UNICAST";
    private final String type = "ERROR";
    private String playerName;
    private String message;

    public ErrorResponse(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
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
