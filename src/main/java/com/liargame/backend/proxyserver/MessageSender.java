package com.liargame.backend.proxyserver;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

public class MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    // 메시지를 클라이언트에게 전송하는 메서드
    public static void sendMessage(WebSocket client, String message) {
        try {
            client.send(message);
            logger.info("클라이언트에 메시지 전송을 완료하였습니다");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(Collection<WebSocket> clients, String message) {
        for (WebSocket client : clients) {
            if (client.isOpen()) { // 연결 상태 확인
                sendMessage(client, message);
            } else {
                logger.warn("해당 웹소켓이 닫혀있어 메세지를 전송할 수 없슨니다.");
            }
        }
    }

}
