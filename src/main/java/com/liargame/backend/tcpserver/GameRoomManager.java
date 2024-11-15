package com.liargame.backend.tcpserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameRoomManager {
    private final Map<String, GameRoom> rooms = new HashMap<>();
    public String createRoom() {
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