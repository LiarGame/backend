package com.liargame.backend.proxyserver;

import org.java_websocket.WebSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketService {
    private static final Map<String, Map<String, WebSocket>> rooms = new ConcurrentHashMap<>();

    public static void addClient(String roomCode, String playerName, WebSocket conn) {
        rooms.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(playerName, conn);
        System.out.println("방 " + roomCode + "에 사용자 " + playerName + " 추가됨.");
    }

    public static void removeClient(String roomCode, String playerName) {
        Map<String, WebSocket> room = rooms.get(roomCode);
        if (room != null) {
            room.remove(playerName);
            if (room.isEmpty()) {
                rooms.remove(roomCode);
                System.out.println("방 " + roomCode + "이 비어 있어 삭제됨.");
            }
        }
    }


    public static void broadcastMessage(String roomCode, String message) {
        Map<String, WebSocket> room = rooms.get(roomCode);
        if (room != null) {
            room.values().forEach(client -> client.send(message));
        }
    }

    public static void unicastMessage(String roomCode, String playerName, String message) {
        Map<String, WebSocket> room = rooms.get(roomCode);
        if (room != null) {
            WebSocket client = room.get(playerName);
            if (client != null) {
                client.send(message);
            }
        }
    }

}
