package com.liargame.backend.proxyserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpConnectionManager {
    private static Socket tcpSocket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void initializeConnection(String host, int port) throws IOException {
        tcpSocket = new Socket(host, port);
        out = new PrintWriter(tcpSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        System.out.println("TCP 서버에 연결되었습니다.");
    }

    public static String sendMessageAndReceiveResponse(String message) throws IOException {
        out.println(message);
        return in.readLine();
    }

    public static void closeConnection() throws IOException {
        if (tcpSocket != null) {
            tcpSocket.close();
        }
    }
}
