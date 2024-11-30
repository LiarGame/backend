package com.liargame.backend.proxyserver;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketService {
    private static final Map<String, Map<String, WebSocket>> rooms = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private static final List<WebSocket> sockets = new ArrayList<>();

    public static void addClient(String roomCode, String playerName, WebSocket conn) {
        rooms.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(playerName, conn);
        logger.info("방 {}에 사용자 {} 추가됨.", roomCode, playerName);
    }

    public static void addClient(WebSocket conn) {
        sockets.add(conn);
        logger.info("방에 사용자 추가됨.");
    }


    // WebSocket 객체로 클라이언트 제거
    public static void removeClient(WebSocket conn) {
        for (String roomCode : rooms.keySet()) {
            Map<String, WebSocket> room = rooms.get(roomCode);

            // 연결 객체를 찾기 위해 방을 순회
            if (room != null) {
                String playerName = null;

                // 연결 객체와 일치하는 플레이어 이름 찾기
                for (Map.Entry<String, WebSocket> entry : room.entrySet()) {
                    if (entry.getValue().equals(conn)) {
                        playerName = entry.getKey();
                        break;
                    }
                }

                // 연결 객체가 발견되면 제거
                if (playerName != null) {
                    room.remove(playerName);
                    logger.info("방 {}에서 사용자 {} 제거됨", roomCode, playerName);
                    return;
                }
            }
        }

        logger.warn("제거할 클라이언트를 찾지 못했습니다 (연결 객체 기준).");
    }

    // WebSocket 객체로 클라이언트 제거
    public static void removeClientInSockets(WebSocket conn) {
        sockets.remove(conn);
    }

    public static void broadcastMessage(String roomCode, String message) {
        Map<String, WebSocket> room = rooms.get(roomCode);
        if (room != null) {
            MessageSender.broadcastMessage(room.values(), message);
        }
    }

    public static void broadcastMessage(String message) {
        MessageSender.broadcastMessage(sockets, message);
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
