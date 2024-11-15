package com.liargame.backend.proxyserver;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

public class MessageHandler {

    public static void handleMessage(String messageJson) {
        JSONObject json = new JSONObject(messageJson);
        String action = json.getString("action");
        String roomCode = json.optString("roomCode");

        switch (action) {
            case "BROADCAST":
                handleBroadcastMessage(roomCode, messageJson);
                break;

            case "UNICAST":
                handleUnicastMessage(roomCode, json);
                break;

            default:
                System.out.println("알 수 없는 액션 타입: " + action);
                break;
        }
    }

    // 방 번호에 따라 모든 클라이언트에게 메시지 브로드캐스트
    private static void handleBroadcastMessage(String roomCode, String messageJson) {
        if (roomCode.isEmpty()) {
            System.out.println("roomCode가 없습니다. 메시지를 브로드캐스트할 수 없습니다.");
            return;
        }

        // 특정 방의 모든 클라이언트에게 메시지 전송
        WebSocketService.broadcastMessage(roomCode, messageJson);
    }

    // 특정 사용자에게 메시지 유니캐스트
    private static void handleUnicastMessage(String roomCode, JSONObject json) {
        if (roomCode.isEmpty()) {
            System.out.println("roomCode가 없습니다. 메시지를 유니캐스트할 수 없습니다.");
            return;
        }

        String targetUsername = json.optString("playerName");
        if (targetUsername.isEmpty()) {
            System.out.println("playerName이 없습니다. 메시지를 유니캐스트할 수 없습니다.");
            return;
        }

        // 특정 사용자에게 메시지 전송
        WebSocketService.unicastMessage(roomCode, targetUsername, json.toString());
    }
}
