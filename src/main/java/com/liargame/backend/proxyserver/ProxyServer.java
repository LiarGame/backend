package com.liargame.backend.proxyserver;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.*;

@ServerEndpoint("/liargame")
public class ProxyServer {

    static {
        try {
            TcpConnectionManager.initializeConnection();
            new Thread(ProxyServer::listenToTcpMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        WebSocketService.addClient(session);
    }

    @OnMessage
    public void onMessage(Session session, String messageJson) {
        try {
            TcpConnectionManager.sendMessage(messageJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        WebSocketService.removeClient(session);
        if (WebSocketService.isEmpty()) {
            try {
                TcpConnectionManager.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
        WebSocketService.removeClient(session);
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listenToTcpMessages() {
        try {
            String tcpMessage;
            while ((tcpMessage = TcpConnectionManager.receiveMessage()) != null) {
                MessageHandler.handleMessage(tcpMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

