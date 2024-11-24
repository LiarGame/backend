package com.liargame.backend.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liargame.backend.message.game.*;
import com.liargame.backend.message.room.CreateRoomRequest;
import com.liargame.backend.message.room.JoinRequest;


public class MessageFactory {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Message createMessage(String messageJson) {
        try {
            // JSON 파싱
            JsonNode rootNode = objectMapper.readTree(messageJson);
            String type = rootNode.path("type").asText();
            // type에 따라 Request 관련 객체 생성 및 데이터 설정
            switch (type) {
                case "CREATE_ROOM_REQUEST" -> {
                    String playerName = rootNode.path("playerName").asText();
                    return new CreateRoomRequest(playerName);
                }
                case "JOIN_REQUEST" -> {
                    String playerName = rootNode.path("playerName").asText();
                    String roomCode = rootNode.path("roomCode").asText();
                    return new JoinRequest(playerName, roomCode);
                }
                case "START_GAME_REQUEST" -> {
                    String playerName = rootNode.path("playerName").asText();
                    String roomCode = rootNode.path("roomCode").asText();
                    return new StartGameRequest(playerName, roomCode);
                }
                case "SPEAK_REQUEST" -> {
                    String playerName = rootNode.path("playerName").asText();
                    String roomCode = rootNode.path("roomCode").asText();
                    String message = rootNode.path("message").asText();
                    return new SpeakRequest(playerName, message, roomCode);
                }
                case "DISCUSS_MESSAGE_REQUEST" -> {
                    String playerName = rootNode.path("playerName").asText();
                    String roomCode = rootNode.path("roomCode").asText();
                    String message = rootNode.path("message").asText();
                    return new DiscussMessageRequest(playerName, message, roomCode);
                }
                case "VOTE_REQUEST" -> {
                    String voter = rootNode.path("voter").asText();
                    String suspect = rootNode.path("suspect").asText();
                    String roomCode = rootNode.path("roomCode").asText();
                    return new VoteRequest(voter,suspect,roomCode);
                }
                case "GUESS_REQUEST" -> {
                    String playerName = rootNode.path("playerName").asText();
                    String roomCode = rootNode.path("roomCode").asText();
                    String guessWord = rootNode.path("guessWord").asText();
                    return new GuessWordRequest(playerName, roomCode, guessWord);
                }
                default -> {
                    throw new IllegalArgumentException("정의되지 않은 메시지 타입: " + type);
                }

            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse message JSON", e);
        }
    }
}
