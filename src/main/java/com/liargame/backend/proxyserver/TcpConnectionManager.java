package com.liargame.backend.proxyserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TcpConnectionManager {
    private static Socket tcpSocket;
    private static ObjectOutputStream tcpObjectOutputStream;
    private static ObjectInputStream tcpObjectInputStream;

    public static void initializeConnection() throws IOException {
        tcpSocket = new Socket("localhost", 10001);
        tcpObjectOutputStream = new ObjectOutputStream(tcpSocket.getOutputStream());
        tcpObjectInputStream = new ObjectInputStream(tcpSocket.getInputStream());
    }

    public static void closeConnection() throws IOException {
        if (tcpObjectOutputStream != null) tcpObjectOutputStream.close();
        if (tcpObjectInputStream != null) tcpObjectInputStream.close();
        if (tcpSocket != null && !tcpSocket.isClosed()) tcpSocket.close();
    }

    public static void sendMessage(Object message) throws IOException {
        tcpObjectOutputStream.writeObject(message);
        tcpObjectOutputStream.flush();
    }

    public static String receiveMessage() throws IOException {
        return tcpObjectInputStream.readLine();
    }
}
