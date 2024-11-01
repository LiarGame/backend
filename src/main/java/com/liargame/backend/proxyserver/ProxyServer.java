package com.liargame.backend.proxyserver;

import org.json.JSONObject;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ServerEndpoint("/liargame")
public class ProxyServer {
    private static Set<Session> clients = new HashSet<>();
    private static Socket tcpSocket;
    private static BufferedWriter tcpWriter;
    private static BufferedReader tcpReader;

    // 정적 초기화 블록에서 연결 설정과 스레드 시작 메서드 호출
    static {
        initializeTcpConnection();
        startTcpResponseListener();
    }

    // TCP 서버와 연결 설정 메서드
    private static void initializeTcpConnection() {
        try {
            tcpSocket = new Socket("localhost", 10001); // TCP 서버의 주소와 포트
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

    // WebSocket 클라이언트로부터 메시지를 수신하여 TCP 서버로 전달
    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            // TCP 서버에 WebSocket 클라이언트의 메시지 전송
            tcpWriter.write(message + "\n");  // 메시지 끝에 줄바꿈 추가하여 메시지 구분
            tcpWriter.flush();
            System.out.println("TCP 서버로 메시지를 전송했습니다: " + message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("TCP 서버로 메시지 전송 중 오류 발생: " + message);
        }
    }


    private static void startTcpResponseListener() {
        new Thread(() -> receiveTcpMessages()).start();
    }

    // TCP 서버로부터 메시지를 수신하고 처리하는 메서드
    private static void receiveTcpMessages() {
        String tcpResponse;
        try {
            while ((tcpResponse = tcpReader.readLine()) != null) {
                handleTcpMessage(tcpResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TCP 메시지를 처리하여 action에 따라 전송 방식 결정
    private static void handleTcpMessage(String tcpMessage) {
        JSONObject json = new JSONObject(tcpMessage);
        String action = json.getString("action");

        switch (action) {
            case "BROADCAST":
                String broadcastContent = json.getString("content");
                MessageSender.broadcastMessage(clients, broadcastContent);
                break;

            case "UNICAST":
                String targetUsername = json.getString("playerName");
                // targetUsername과 일치하는 클라이언트 찾기
                Optional<Session> targetSession = clients.stream()
                        .filter(session -> session.getUserProperties().get("username").equals(targetUsername))
                        .findFirst();

                targetSession.ifPresent(session -> MessageSender.sendMessage(session, tcpMessage));
                break;

            default:
                System.out.println("알 수 없는 action: " + action);
                break;
        }
    }
}
