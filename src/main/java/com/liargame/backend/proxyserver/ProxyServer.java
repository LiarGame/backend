package com.liargame.backend.proxyserver;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.Set;
@ServerEndpoint("/liargame")
public class ProxyServer {
    private static Set<Session> clients = new HashSet<>();

    // 클라이언트가 WebSocket 서버에 연결될 때 호출
    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);  // 클라이언트 세션을 추가
        System.out.println("새로운 클라이언트가 연결되었습니다: " + session.getId());
    }
}
