package com.liargame.backend.proxyserver;

import org.json.JSONObject;
import javax.websocket.Session;
import java.util.Optional;

public class MessageHandler {
    public static void handleMessage(String messageJson) {
        JSONObject json = new JSONObject(messageJson);
        String action = json.getString("action");

        switch (action) {
            case "BROADCAST":
                MessageSender.broadcastMessage(WebSocketService.getClients(), messageJson);
                break;
            case "UNICAST":
                String targetUsername = json.getString("playerName");
                Optional<Session> targetSession = WebSocketService.getClients()
                        .stream()
                        .filter(session -> session.getUserProperties().get("username").equals(targetUsername))
                        .findFirst();
                targetSession.ifPresent(session -> MessageSender.sendMessage(session, messageJson));
                break;
            default:
                break;
        }
    }
}

