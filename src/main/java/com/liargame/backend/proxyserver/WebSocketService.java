package com.liargame.backend.proxyserver;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketService {
    private static final Map<String, Map<String, WebSocket>> rooms = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    public static void addClient(String roomCode, String playerName, WebSocket conn) {
        rooms.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(playerName, conn);
        logger.info("방 {}에 사용자 {} 추가됨.", roomCode, playerName);
    }

    public static void removeClient(String roomCode, String playerName) {
        Map<String, WebSocket> room = rooms.get(roomCode);
        if (room != null) {
            room.remove(playerName);
            if (room.isEmpty()) {
                rooms.remove(roomCode);
                logger.info("방 {}이 비어 있어 삭제됨.", roomCode);
            }
        }
    }

    public static void broadcastMessage(String roomCode, String message) {
        Map<String, WebSocket> room = rooms.get(roomCode);
        if (room != null) {
            MessageSender.broadcastMessage((Set<WebSocket>) room.values(), message); // MessageSender로 위임
        }
    }

    public static void unicastMessage(String roomCode, String playerName, String message) {
        Map<String, WebSocket> room = rooms.get(roomCode);
        if (room != null) {
            WebSocket client = room.get(playerName);
            if (client != null) {
                MessageSender.sendMessage(client, message); // MessageSender로 위임
            }
        }
    }
}
