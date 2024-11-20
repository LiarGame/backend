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

    // 상수 정의
    private static final String TYPE_ERROR = "ERROR";
    private static final String TYPE_JOIN_REQUEST = "JOIN_REQUEST";
    private static final String TYPE_CREATE_ROOM_RESPONSE = "CREATE_ROOM_RESPONSE";
    private static final String ACTION_BROADCAST = "BROADCAST";
    private static final String ACTION_UNICAST = "UNICAST";

    public static void handleClientMessage(WebSocket client, String clientMessage) {
        executorService.submit(() -> {
            try {
                logger.info("클라이언트에서 메시지가 도착하였습니다 : "+clientMessage);

                // 메시지 파싱
                JSONObject clientMessageJson = new JSONObject(clientMessage);
                String clientMessageType = clientMessageJson.getString("type");
                String roomCode = clientMessageJson.optString("roomCode");
                String playerName = clientMessageJson.optString("playerName");

                // JOIN_REQUEST 처리
                if (clientMessageType.equals(TYPE_JOIN_REQUEST)) {
                    handleJoinRequest(client, roomCode, playerName);
                }

                // TCP 서버로 메시지 전송 및 응답 처리
                Message clientMessageObject = MessageFactory.createMessage(clientMessage);
                Response tcpResponse = TcpConnectionManager.sendMessageAndReceiveResponse(clientMessageObject);

                if (tcpResponse == null) {
                    logger.warn("TCP 서버로부터 응답을 받지 못했습니다.");
                    sendErrorMessage(client, "TCP 서버로부터 응답을 받지 못했습니다.");
                    return;
                }

                handleTCPResponse(client, tcpResponse);
            } catch (Exception e) {
                logger.error("메시지 처리 중 오류 발생", e);
                sendErrorMessage(client, "서버 오류가 발생했습니다.");
            }
        });
    }

    private static void handleJoinRequest(WebSocket client, String roomCode, String playerName) {
        if (isNullOrEmpty(roomCode, "roomCode") || isNullOrEmpty(playerName, "playerName")) {
            sendErrorMessage(client, "roomCode 또는 playerName이 유효하지 않습니다.");
            return;
        }

        WebSocketService.addClient(roomCode, playerName, client);
        logger.info("클라이언트가 방에 참여했습니다: roomCode={}, playerName={}", roomCode, playerName);
    }

    private static void handleTCPResponse(WebSocket client, Response tcpResponse) {
        try {
            JSONObject responseJson = new JSONObject(tcpResponse);
            logger.info("TCP 서버의 응답: {}", responseJson);

            String type = responseJson.getString("type");
            String roomCode = responseJson.optString("roomCode");
            String playerName = responseJson.optString("playerName");
            String action = tcpResponse.getAction();

            // ERROR 처리
            if (type.equals(TYPE_ERROR)) {
                sendErrorMessageByTCPServer(client, responseJson);
                return;
            }

            // CREATE_ROOM_RESPONSE 처리
            if (type.equals(TYPE_CREATE_ROOM_RESPONSE)) {
                WebSocketService.addClient(roomCode, playerName, client);
                logger.info("클라이언트가 방에 참여했습니다: roomCode={}, playerName={}", roomCode, playerName);
            }

            // 액션 처리
            switch (action) {
                case ACTION_BROADCAST:
                    handleBroadcastMessage(roomCode, responseJson.toString());
                    break;

                case ACTION_UNICAST:
                    handleUnicastMessage(roomCode, responseJson);
                    break;

                default:
                    logger.warn("알 수 없는 응답 액션 타입: {}", action);
                    sendErrorMessage(client, "알 수 없는 응답 액션 타입입니다.");
            }
        } catch (Exception e) {
            logger.error("TCP 응답 처리 중 오류 발생", e);
            sendErrorMessage(client, "TCP 응답 처리 중 오류가 발생했습니다.");
        }
    }

    private static void handleBroadcastMessage(String roomCode, String messageJson) {
        if (isNullOrEmpty(roomCode, "roomCode")) return;

        logger.info("roomCode={}에 메시지를 브로드캐스트합니다.", roomCode);
        WebSocketService.broadcastMessage(roomCode, messageJson);
    }

    private static void handleUnicastMessage(String roomCode, JSONObject json) {
        if (isNullOrEmpty(roomCode, "roomCode")) return;

        String targetPlayerName = json.optString("playerName");
        if (isNullOrEmpty(targetPlayerName, "playerName")) return;

        logger.info("roomCode={}, playerName={}에게 메시지를 유니캐스트합니다.", roomCode, targetPlayerName);
        WebSocketService.unicastMessage(roomCode, targetPlayerName, json.toString());
    }

    private static void sendErrorMessage(WebSocket client, String errorMessage) {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("type", TYPE_ERROR);
        errorResponse.put("message", errorMessage);
        client.send(errorResponse.toString());
    }

    private static void sendErrorMessageByTCPServer(WebSocket client, JSONObject errorJson) {
        client.send(errorJson.toString());
        logger.warn("클라이언트에 에러 메시지를 전송하였습니다");

    }

    private static boolean isNullOrEmpty(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            logger.warn("{}이(가) 없습니다.", fieldName);
            return true;
        }
        return false;
    }
}
