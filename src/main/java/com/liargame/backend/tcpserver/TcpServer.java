package com.liargame.backend.tcpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);
    private final GameRoomManager gm = new GameRoomManager();

    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(10001);
            logger.info("접속을 기다립니다.");
            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("Web Socket 연결 성공");
                Thread tcpThread = new Thread(new ClientHandler(socket, gm));
                tcpThread.start();
            }
        } catch (IOException e) {
            logger.error("Web Socket 연결 실패");
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
