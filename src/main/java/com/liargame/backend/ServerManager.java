package com.liargame.backend;

import com.liargame.backend.tcpserver.TcpServer;
import com.liargame.backend.proxyserver.ProxyServer;

public class ServerManager {
    private TcpServer tcpServer;
    private ProxyServer webSocketServer;
    private Thread tcpServerThread;
    private Thread webSocketServerThread;

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
                System.err.println("TCP 서버 실행 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        });
        tcpServerThread.setName("TCP-Server-Thread");
        tcpServerThread.start();
        System.out.println("TCP 서버가 시작되었습니다.");
    }

    // WebSocket 서버 시작
    private void startWebSocketServer() {
        int webSocketPort = 8080;
        webSocketServer = new ProxyServer(webSocketPort);
        webSocketServerThread = new Thread(() -> {
            try {
                webSocketServer.start();
            } catch (Exception e) {
                System.err.println("WebSocket 서버 실행 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        });
        webSocketServerThread.setName("WebSocket-Server-Thread");
        webSocketServerThread.start();
        System.out.println("WebSocket 서버가 포트 " + webSocketPort + "에서 시작되었습니다.");
    }

    // 서버 종료 처리
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("애플리케이션 종료 중...");
            stopTcpServer();
            stopWebSocketServer();
            System.out.println("모든 서버가 정상적으로 종료되었습니다.");
        }));
    }

    // TCP 서버 종료
    private void stopTcpServer() {
        if (tcpServer != null) {
            try {
                // todo: tcp server 종료 구현
                // tcpServer.stop();
                System.out.println("TCP 서버가 종료되었습니다.");
            } catch (Exception e) {
                System.err.println("TCP 서버 종료 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // WebSocket 서버 종료
    private void stopWebSocketServer() {
        if (webSocketServer != null) {
            try {
                webSocketServer.stop();
                System.out.println("WebSocket 서버가 종료되었습니다.");
            } catch (Exception e) {
                System.err.println("WebSocket 서버 종료 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
