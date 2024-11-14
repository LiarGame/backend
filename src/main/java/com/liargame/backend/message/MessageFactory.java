package com.liargame.backend.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class MessageFactory {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Message createMessage(String messageJson) {
        try {
            // JSON 파싱
            JsonNode rootNode = objectMapper.readTree(messageJson);
            String type = rootNode.path("type").asText();

            // type에 따라 객체 생성 및 데이터 설정
            switch (type) {
                case "CREATE_ROOM_REQUEST" -> {
                    String playerName = rootNode.path("playerName").asText();
                    return new RoomCreateRequest(playerName);
                }
                case "CREATE_ROOM_RESPONSE" -> {
                    String roomCode = rootNode.path("roomCode").asText();
                    return new RoomCreateResponse(type, roomCode);
                }
                case "JOIN_REQUEST" -> {
                    String playerName = rootNode.path("playerName").asText();
                    String roomCode = rootNode.path("roomCode").asText();
                    return new JoinRequest(playerName, roomCode);
                }
                case "JOIN_RESPONSE" -> {
                    List<String> playerList = Collections.singletonList(rootNode.path("playerList").asText());
                    String roomCode = rootNode.path("roomCode").asText();
                    return new JoinResponse(playerList, roomCode);
                }
                case "START_GEME_REQUEST" -> {
                    String playerName = rootNode.path("playerName").asText();
                    String roomCode = rootNode.path("roomCode").asText();
                    return new StartGameRequest(playerName, roomCode);
                }
                case "ROLE_ASSIGN_RESPONSE" -> {
                    String liar = rootNode.path("liar").asText();
                    String topic = rootNode.path("topic").asText();
                    String word = rootNode.path("word").asText();
                    String roomCode = rootNode.path("roomCode").asText();
                    return new RoleAssignResponse(liar, topic, word, roomCode);
                }
                default -> {
                    throw new IllegalArgumentException("Unknown message type: " + type);
                }

            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse message JSON", e);
        }
    }
}
