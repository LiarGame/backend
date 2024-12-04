package com.liargame.backend.proxyserver;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public WebSocketServer(String host, int port, String tcpHost, int tcpPort) {
        super(new InetSocketAddress(host, port)); // 호스트와 포트를 명시적으로 설정
        try {
            TcpConnectionManager.initializeConnection(tcpHost, tcpPort);
            logger.info("웹소켓 서버에서 TCP 서버와의 연결이 초기화되었습니다.");
        } catch (IOException e) {
            logger.error("TCP 서버와의 연결 초기화 중 오류 발생", e);
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("웹소켓 서버와 클라이언트와의 새 연결이 시작되었습니다.");
    }


    @Override
    public void onMessage(WebSocket conn, String messageJson) {
        executorService.submit(() -> {
            try {
                JSONObject message = new JSONObject(messageJson);
                String type = message.getString("type");

                if (type.equals("RECONNECT_REQUEST")) {
                    String playerName = message.getString("playerName");
                    String roomCode = message.getString("roomCode");

                    // 이미 방에 참여하고 있는 경우, 상태 덮어쓰지 않고 연결만 복구
                    WebSocketService.addClient(roomCode, playerName, conn);
                    logger.info("클라이언트 {}가 방 {}에 재참여했습니다.", playerName, roomCode);
                } else {
                    MessageHandler.handleClientMessage(conn, messageJson);
                }
            } catch (Exception e) {
                logger.error("메시지 처리 중 오류 발생", e);
                conn.send("{\"type\": \"ERROR\", \"message\": \"서버 오류가 발생했습니다.\"}");
            }
        });
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("웹소켓 연결이 종료되었습니다. ID: {}, 코드: {}, 이유: {}, 원격 종료 여부: {}",
                conn.hashCode(), code, reason, remote);
        WebSocketService.removeClient(conn); // 연결 객체 기준으로 클라이언트 제거
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (conn != null) {
            logger.error("웹 소켓 서버에서 오류 발생. 클라이언트 주소: {}, ID: {}", conn.getRemoteSocketAddress(), conn.hashCode(), ex);
        } else {
            logger.error("웹 소켓 서버에서 연결되지 않은 클라이언트 오류 발생", ex);
        }
    }

    @Override
    public void onStart() {
        logger.info("웹 소켓 서버가 시작되었습니다.");
    }
}