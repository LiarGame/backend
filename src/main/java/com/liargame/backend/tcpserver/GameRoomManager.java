package com.liargame.backend.tcpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameRoomManager {
    private static final Logger logger = LoggerFactory.getLogger(GameRoomManager.class);
    private final Map<String, GameRoom> rooms = new HashMap<>();
    public String createRoom() {
        logger.info("방을 생성합니다.");
        String roomCode = generateRoomCode();
        GameRoom gameRoom = new GameRoom(roomCode);
        rooms.put(roomCode, gameRoom);
        return roomCode;
    }

    public Map<String, GameRoom> getRooms() {
        return rooms;
    }

    public GameRoom getRoom(String roomCode) {
        synchronized (rooms) {
            return rooms.get(roomCode);
        }
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}