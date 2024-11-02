package com.liargame.backend.proxyserver;

import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


@ServerEndpoint("/liargame")
public class ProxyServer {
    private static Set<Session> clients = new HashSet<>();
    private static Socket tcpSocket;
    private static BufferedWriter tcpWriter;
    private static BufferedReader tcpReader;
    private static final Logger logger = Logger.getLogger(ProxyServer.class.getName());

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

    private static void startTcpResponseListener() {
        new Thread(() -> receiveTcpMessages()).start();
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

    // WebSocket 클라이언트 연결 종료 처리
    @OnClose
    public void onClose(Session session) {
        clients.remove(session);  // 클라이언트 세션을 제거
        System.out.println("클라이언트 연결이 종료되었습니다: " + session.getId());

        // 모든 클라이언트가 종료된 경우 TCP 연결 닫기
        if (clients.isEmpty()) {
            closeTcpConnection();
        }
    }

    // WebSocket 에러 처리
    @OnError
    public void onError(Session session, Throwable throwable) {
        // 에러 로그 기록
        logger.log(Level.SEVERE, "WebSocket 에러 발생 - 세션 ID: " + session.getId(), throwable);

        // 에러가 발생한 세션을 제거하고 닫기
        try {
            clients.remove(session);
            session.close();
            logger.info("에러 발생으로 인해 세션이 닫혔습니다: " + session.getId());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "세션 종료 중 오류 발생 - 세션 ID: " + session.getId(), e);
        }
    }

    // TCP 서버와의 연결을 닫고 자원 해제
    private static void closeTcpConnection() {
        try {
            if (tcpWriter != null) tcpWriter.close();
            if (tcpReader != null) tcpReader.close();
            if (tcpSocket != null && !tcpSocket.isClosed()) tcpSocket.close();
            System.out.println("TCP 서버와의 연결이 종료되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                MessageSender.broadcastMessage(clients, tcpMessage);
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
