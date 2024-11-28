package com.liargame.backend.proxyserver;

import com.liargame.backend.message.Message;
import com.liargame.backend.message.Response;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
        // 비동기로 TCP 서버에 메시지 전송
        executorService.submit(() -> {
            try {
                // TCP 서버에 메시지 전송 및 응답 받기
                MessageHandler.handleClientMessage(conn, messageJson);

            } catch (Exception e) {
                logger.error("서버 내부 오류 발생", e);
                conn.send("{\"type\": \"ERROR\", \"message\": \"서버 내부 오류가 발생했습니다.\"}");
            }
        });
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("웹소켓 연결이 종료되었습니다. 코드: {}, 이유: {}, 원격 종료 여부: {}", code, reason, remote);
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


    /**
     * 쿼리 문자열을 파싱하여 Map으로 반환하는 유틸리티 메서드
     */
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new ConcurrentHashMap<>();
        if (query != null && query.startsWith("/?")) {
            query = query.substring(2); // "/?" 제거
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
}
