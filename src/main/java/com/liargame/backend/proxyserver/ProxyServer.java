package com.liargame.backend.proxyserver;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
@ServerEndpoint("/liargame")
public class ProxyServer {
    private static Set<Session> clients = new HashSet<>();
    private static Socket tcpSocket;
    private static BufferedWriter tcpWriter;
    private static BufferedReader tcpReader;
    // 서버가 시작될 때 TCP 서버와 연결 설정
    static {
        try {
            tcpSocket = new Socket("localhost", 12345); // TCP 서버의 주소와 포트
            tcpWriter = new BufferedWriter(new OutputStreamWriter(tcpSocket.getOutputStream()));
            tcpReader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 클라이언트가 WebSocket 서버에 연결될 때 호출
    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);  // 클라이언트 세션을 추가
        System.out.println("새로운 클라이언트가 연결되었습니다: " + session.getId());
    }
}
