package com.liargame.backend.proxyserver;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MessageSender {

    // 메시지를 클라이언트에게 전송하는 기본 메서드
    static void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 모든 클라이언트에게 메시지 브로드캐스트
    public static void broadcastMessage(Set<Session> clients, String message) {
        for (Session client : clients) {
            sendMessage(client, message);
        }
    }
}


