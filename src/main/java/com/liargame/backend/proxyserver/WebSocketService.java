package com.liargame.backend.proxyserver;

import javax.websocket.Session;
import java.util.Set;

public class WebSocketService {
    private static Set<Session> clients;

    public static void addClient(Session session) {
        clients.add(session);
    }

    public static void removeClient(Session session) {
        clients.remove(session);
    }

    public static boolean isEmpty() {
        return clients.isEmpty();
    }

    public static Set<Session> getClients() {
        return clients;
    }
}
