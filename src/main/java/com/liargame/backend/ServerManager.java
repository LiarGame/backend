package com.liargame.backend;

import com.liargame.backend.config.Config;
import com.liargame.backend.config.ConfigLoader;
import com.liargame.backend.proxyserver.HttpServer;
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
        config = ConfigLoader.loadConfig("server-config.json");
    }

    public void startServers() {
        startTcpServer();
        startWebSocketServer();
        startHttpServer();
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
        String webSocketHost = config.getWebsocket().getHost();
        int webSocketPort = config.getWebsocket().getPort();
        String tcpHost = config.getTcp().getHost();
        int tcpPort = config.getTcp().getPort();
        logger.info("웹 소켓 서버 ip 주소 및 포트: webSocketHost={}, webSocketPort={}", webSocketHost, webSocketPort);
        logger.info("tcp 서버 ip 주소 및 포트: tcpHost={},tcpPort={}", tcpHost, tcpPort);

        webSocketServer = new WebSocketServer(webSocketHost, webSocketPort, tcpHost, tcpPort);
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

    private void startHttpServer() {
        Thread httpServerThread = new Thread(() -> {
            try {
                HttpServer.start();
            } catch (Exception e) {
                logger.error("HTTP 서버 실행 중 오류 발생: {}", e.getMessage(), e);
            }
        });
        httpServerThread.setName("HTTP-Server-Thread");
        httpServerThread.start();
        logger.info("HTTP 서버가 시작되었습니다.");
    }
}
