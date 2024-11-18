package com.liargame.backend.tcpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private final GameRoomManager gm = new GameRoomManager();

    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(10001);
            System.out.println("접속을 기다립니다.");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Web Socket 연결 성공");
                Thread tcpThread = new Thread(new ClientHandler(socket, gm));
                tcpThread.start();
            }
        } catch (IOException e) {
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
