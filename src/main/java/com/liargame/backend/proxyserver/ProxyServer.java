package com.liargame.backend.proxyserver;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.io.IOException;

public class ProxyServer extends WebSocketServer {
    private final int TCP_PORT = 10001;

    public ProxyServer(int port) {
        super(new InetSocketAddress(port));
        try {
            TcpConnectionManager.initializeConnection("localhost", TCP_PORT);
            System.out.println("TCP 서버와의 연결이 초기화되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("새 연결이 시작되었습니다.");
    }

    @Override
    public void onMessage(WebSocket conn, String messageJson) {
        try {
            JSONObject json = new JSONObject(messageJson);
            String type = json.getString("type");
            String roomCode = json.optString("roomCode");
            String playerName = json.optString("playerName");

            if (type.equals("CREATE_ROOM_REQUEST") || type.equals("JOIN_REQUEST")) {
                WebSocketService.addClient(roomCode, playerName, conn);
            }

            String tcpResponse = TcpConnectionManager.sendMessageAndReceiveResponse(messageJson);
            conn.send(tcpResponse);
        } catch (Exception e) {
            e.printStackTrace();
            conn.send("{\"type\": \"ERROR\", \"message\": \"서버 오류가 발생했습니다.\"}");
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("클라이언트 연결이 종료되었습니다.");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket 프록시 서버가 시작되었습니다.");
    }

    public void stopServer() {
        try {
            this.stop();
            TcpConnectionManager.closeConnection();
            System.out.println("WebSocket 및 TCP 서버가 정상적으로 종료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
