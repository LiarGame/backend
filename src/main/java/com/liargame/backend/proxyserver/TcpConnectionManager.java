package com.liargame.backend.proxyserver;

import com.liargame.backend.message.Message;
import com.liargame.backend.message.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class TcpConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(TcpConnectionManager.class);
    private static Socket tcpSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    // TCP 서버 초기화
    public static void initializeConnection(String host, int port) throws IOException {
        try {
            tcpSocket = new Socket(host, port);
            out = new ObjectOutputStream(tcpSocket.getOutputStream());
            in = new ObjectInputStream(tcpSocket.getInputStream());
            logger.info("TCP 서버 {}:{}에 연결되었습니다.", host, port);
        } catch (IOException e) {
            logger.error("TCP 서버 연결 중 오류 발생: {}:{}", host, port, e);
            throw e;
        }
    }

    // TCP 서버로 메시지를 전송하고 응답을 받는 메서드
    public static synchronized Response sendMessageAndReceiveResponse(Message message) {
        try {
            // 메시지 전송
            out.writeObject(message);
            out.flush();
            // TCP 서버로부터 응답 수신
            Object responseObject = in.readObject();
            if (responseObject instanceof Response) {
                Response response = (Response) responseObject;
                return response;
            } else {
                logger.warn("TCP 서버로부터 알 수 없는 형식의 응답을 받았습니다.");
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("TCP 서버와의 통신 중 오류 발생", e);
            return null;
        }
    }

    // TCP 연결 종료
    public static void closeConnection() {
        try {
            if (tcpSocket != null && !tcpSocket.isClosed()) {
                tcpSocket.close();
                logger.info("TCP 서버와의 연결이 종료되었습니다.");
            }
        } catch (IOException e) {
            logger.error("TCP 연결 종료 중 오류 발생", e);
        }
    }
}
