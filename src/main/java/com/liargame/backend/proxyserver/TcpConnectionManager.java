package com.liargame.backend.proxyserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(TcpConnectionManager.class);
    private static Socket tcpSocket;
    private static PrintWriter out;
    private static BufferedReader in;

    // TCP 서버 초기화
    public static void initializeConnection(String host, int port) throws IOException {
        try {
            tcpSocket = new Socket(host, port);
            out = new PrintWriter(tcpSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            logger.info("웹 소켓 서버에서 TCP 서버 {}:{}에 연결되었습니다.", host, port);
        } catch (IOException e) {
            logger.error("웹 소켓 서버에서 TCP 서버 연결 중 오류 발생: {}:{}", host, port, e);
            throw e;
        }
    }

    // 메시지 전송 및 응답 수신
    public static String sendMessageAndReceiveResponse(String message) throws IOException {
        try {
            logger.debug("TCP 서버로 메시지 전송: {}", message);
            out.println(message);
            String response = in.readLine();
            logger.debug("TCP 서버로부터 응답 수신: {}", response);
            return response;
        } catch (IOException e) {
            logger.error("메시지 전송 또는 응답 수신 중 오류 발생", e);
            throw e;
        }
    }

    // TCP 연결 종료
    public static void closeConnection() {
        try {
            if (tcpSocket != null && !tcpSocket.isClosed()) {
                tcpSocket.close();
                logger.info("웹 소켓 서버에서 TCP 서버와의 연결이 종료되었습니다.");
            }
        } catch (IOException e) {
            logger.error("웹 소켓 서버에서 TCP 연결 종료 중 오류 발생", e);
        }
    }
}
