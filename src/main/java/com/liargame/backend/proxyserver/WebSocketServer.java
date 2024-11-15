package com.liargame.backend.proxyserver;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.io.IOException;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private final int TCP_PORT = 10001;

    public WebSocketServer(int port) {
        super(new InetSocketAddress(port));
        try {
            TcpConnectionManager.initializeConnection("localhost", TCP_PORT);
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
        try {
            JSONObject json = new JSONObject(messageJson);
            String type = json.getString("type");
            String roomCode = json.optString("roomCode");
            String playerName = json.optString("playerName");

            if (type.equals("CREATE_ROOM_REQUEST") || type.equals("JOIN_REQUEST")) {
                WebSocketService.addClient(roomCode, playerName, conn);
                logger.info("클라이언트가 방에 참여했습니다: roomCode={}, playerName={}", roomCode, playerName);
            }

            String tcpResponse = TcpConnectionManager.sendMessageAndReceiveResponse(messageJson);
            conn.send(tcpResponse);
        } catch (Exception e) {
            logger.error("메시지 처리 중 오류 발생", e);
            conn.send("{\"type\": \"ERROR\", \"message\": \"웹 소켓 서버에서 서버 오류가 발생했습니다.\"}");
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("웹 소켓 서버에서 클라이언트 연결이 종료되었습니다. 이유: {}", reason);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("웹 소켓 서버에서 오류 발생", ex);
    }

    @Override
    public void onStart() {
        logger.info("웹 소켓 서버가 시작되었습니다.");
    }

    public void stopServer() {
        try {
            this.stop();
            TcpConnectionManager.closeConnection();
            logger.info("웹 소켓 서버에서 TCP 서버와의 연결이 정상적으로 종료되었습니다.");
        } catch (Exception e) {
            logger.error("서버 종료 중 오류 발생", e);
        }
    }
}
