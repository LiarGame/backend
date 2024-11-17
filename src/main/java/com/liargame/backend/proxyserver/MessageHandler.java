package com.liargame.backend.proxyserver;

import com.liargame.backend.message.Message;
import com.liargame.backend.message.MessageFactory;
import com.liargame.backend.message.Response;
import org.java_websocket.WebSocket;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void handleClientMessage(WebSocket client, String messageJson) {
        executorService.submit(() -> {
            try {
                Message clientMessage = MessageFactory.createMessage(messageJson);
                // TCP 서버로 메시지 전송 및 응답 수신
                Response tcpResponse = TcpConnectionManager.sendMessageAndReceiveResponse(clientMessage);

                if (tcpResponse == null) {
                    logger.warn("TCP 서버로부터 응답을 받지 못했습니다.");
                    sendErrorMessage(client, "TCP 서버로 부터 응답을 받지 못해 오류가 발생했습니다.");
                    return;
                }

                // TCP 응답 처리
                JSONObject responseJson = new JSONObject(tcpResponse);
                String responseAction = tcpResponse.getAction();
                String roomCode = responseJson.getString("roomCode");
                switch (responseAction) {
                    case "BROADCAST":
                        handleBroadcastMessage(roomCode, String.valueOf(responseJson));
                        break;

                    case "UNICAST":
                        handleUnicastMessage(roomCode, responseJson);
                        break;

                    default:
                        logger.warn("알 수 없는 응답 액션 타입: {}", responseAction);
                        sendErrorMessage(client, "알 수 없는 응답 액션 타입입니다.");
                        break;
                }
            } catch (Exception e) {
                logger.error("메시지 처리 중 오류 발생", e);
                sendErrorMessage(client, "서버 오류가 발생했습니다.");
            }
        });
    }

    private static void handleBroadcastMessage(String roomCode, String messageJson) {
        if (roomCode.isEmpty()) {
            logger.warn("roomCode가 없습니다. 메시지를 브로드캐스트할 수 없습니다.");
            return;
        }

        logger.info("roomCode={} 에 메시지를 브로드캐스트합니다.", roomCode);
        WebSocketService.broadcastMessage(roomCode, messageJson);
    }

    private static void handleUnicastMessage(String roomCode, JSONObject json) {
        if (roomCode.isEmpty()) {
            logger.warn("roomCode가 없습니다. 메시지를 유니캐스트할 수 없습니다.");
            return;
        }

        String targetUsername = json.optString("playerName");
        if (targetUsername.isEmpty()) {
            logger.warn("playerName이 없습니다. 메시지를 유니캐스트할 수 없습니다.");
            return;
        }

        logger.info("roomCode={}, playerName={} 에게 메시지를 유니캐스트합니다.", roomCode, targetUsername);
        WebSocketService.unicastMessage(roomCode, targetUsername, json.toString());
    }

    private static void sendErrorMessage(WebSocket client, String errorMessage) {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("type", "ERROR");
        errorResponse.put("message", errorMessage);
        client.send(errorResponse.toString());
    }
}
