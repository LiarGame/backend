package com.liargame.backend;

import com.liargame.backend.tcpserver.TcpServer;
import com.liargame.backend.proxyserver.ProxyServer;

public class Main {
    public static void main(String[] args) {
        // TCP 서버 스레드 생성 및 실행
        Thread tcpServerThread = new Thread(() -> {
            TcpServer tcpServer = new TcpServer();
            tcpServer.start();
        });

        // WebSocket 서버 스레드 생성 및 실행
        Thread webSocketServerThread = new Thread(() -> {
            int webSocketPort = 8080;
            ProxyServer proxyServer = new ProxyServer(webSocketPort);
            proxyServer.start();
        });

        // 스레드 시작
        tcpServerThread.start();
        webSocketServerThread.start();

        // 애플리케이션 종료 시 서버 종료 관리
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("애플리케이션 종료 중...");
            try {
                tcpServerThread.interrupt();
                webSocketServerThread.interrupt();
                System.out.println("서버가 종료되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
