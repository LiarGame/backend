package com.liargame.backend.proxyserver;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.Set;

public class MessageSender {

    // 메시지를 클라이언트에게 전송하는 메서드
    static void sendMessage(WebSocket client, String message) {
        try {
            client.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 모든 클라이언트에게 메시지 브로드캐스트
    public static void broadcastMessage(Set<WebSocket> clients, String message) {
        for (WebSocket client : clients) {
            sendMessage(client, message);
        }
    }
}
