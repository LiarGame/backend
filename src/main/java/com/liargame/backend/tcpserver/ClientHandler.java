package com.liargame.backend.tcpserver;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private GameRoomManager gm;

    public ClientHandler(Socket socket, GameRoomManager gm) {
        this.socket = socket;
        this.gm = gm;
    }

    @Override
    public void run() {
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            String request;
            while ((request = br.readLine()) != null) {
                String type = getType(request);
                String userName = getName(request);
                System.out.println("요청: " + type);
                switch (type) {
                    case "CREATE_ROOM" -> {
                        String roomCode = gm.createRoom(userName);
                        pw.println("{ \"type\": \"ROOM_CREATED\", \"roomCode\": \"" + roomCode + "\" }");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                if (pw != null) pw.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static String getFieldValue(String request, String fieldName, String defaultValue, String errorMessage) {
        String key = "\"" + fieldName + "\":";
        int keyIndex = request.indexOf(key);

        if (keyIndex != -1) {
            try {
                int startIndex = request.indexOf("\"", keyIndex + key.length()) + 1;
                int endIndex = request.indexOf("\"", startIndex);

                if (endIndex != -1) {
                    return request.substring(startIndex, endIndex);
                } else {
                    throw new IllegalArgumentException("'" + fieldName + "' 필드가 존재하지 않습니다.");
                }
            } catch (Exception e) {
                System.err.println(errorMessage + " - " + e.getMessage());
                return defaultValue;
            }
        } else {
            System.err.println(errorMessage);
            return defaultValue;
        }
    }

    public static String getType(String request) {
        return getFieldValue(request, "type", "MISSED_TYPE", "요청 타입이 존재하지 않습니다.");
    }

    public static String getName(String request) {
        return getFieldValue(request, "playerName", "MISSED_NAME", "사용자의 이름이 존재하지 않습니다.");
    }
}
