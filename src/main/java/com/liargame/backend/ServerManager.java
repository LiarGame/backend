package com.liargame.backend;

import com.liargame.backend.config.Config;
import com.liargame.backend.config.ConfigLoader;
import com.liargame.backend.tcpserver.TcpServer;
import com.liargame.backend.proxyserver.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerManager {
    private TcpServer tcpServer;
    private WebSocketServer webSocketServer;
    private Thread tcpServerThread;
    private Thread webSocketServerThread;
    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);
    private Config config;

    public ServerManager() {
        // JSON 파일에서 설정 로드
        config = ConfigLoader.loadConfig("config.json");
    }

    public void startServers() {
        startTcpServer();
        startWebSocketServer();
        addShutdownHook();
    }

    // TCP 서버 시작
    private void startTcpServer() {
        tcpServer = new TcpServer();
        tcpServerThread = new Thread(() -> {
            try {
                tcpServer.start();
            } catch (Exception e) {
                logger.error("TCP 서버 실행 중 오류 발생: {}", e.getMessage(), e);
            }
        });
        tcpServerThread.setName("TCP-Server-Thread");
        tcpServerThread.start();
        logger.info("TCP 서버가 시작되었습니다.");
    }

    // WebSocket 서버 시작
    private void startWebSocketServer() {
        String host = config.getWebsocket().getHost();
        int port = config.getWebsocket().getPort();
        logger.info("서버 ip 주소 : " + host + ":" + port);
        webSocketServer = new WebSocketServer(host, port);
        webSocketServerThread = new Thread(() -> {
            try {
                webSocketServer.start();
            } catch (Exception e) {
                logger.error("WebSocket 서버 실행 중 오류 발생: {}", e.getMessage(), e);
            }
        });
        webSocketServerThread.setName("WebSocket-Server-Thread");
        webSocketServerThread.start();
    }

    // 서버 종료 처리
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("애플리케이션 종료 중...");
            stopTcpServer();
            stopWebSocketServer();
            logger.info("모든 서버가 정상적으로 종료되었습니다.");
        }));
    }

    // TCP 서버 종료
    private void stopTcpServer() {
        if (tcpServer != null) {
            try {
                // todo: tcp server 종료 구현
                // tcpServer.stop();
                logger.info("TCP 서버가 종료되었습니다.");
            } catch (Exception e) {
                logger.error("TCP 서버 종료 중 오류 발생: {}", e.getMessage(), e);
            }
        }
    }

    // WebSocket 서버 종료
    private void stopWebSocketServer() {
        if (webSocketServer != null) {
            try {
                webSocketServer.stop();
                logger.info("WebSocket 서버가 종료되었습니다.");
            } catch (Exception e) {
                logger.error("WebSocket 서버 종료 중 오류 발생: {}", e.getMessage(), e);
            }
        }
    }
}
