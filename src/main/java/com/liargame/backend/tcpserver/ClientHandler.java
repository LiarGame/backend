package com.liargame.backend.tcpserver;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private final GameRoomManager gm;

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
                String playerName = getName(request);
                String code = getRoomCode(request);
                System.out.println("요청: " + type);
                switch (type) {
                    case "CREATE_ROOM_REQUEST" -> {
                        String roomCode;
                        GameRoom currentRoom;

                        synchronized (gm) {
                            roomCode = gm.createRoom();
                            currentRoom = gm.getRoom(roomCode);
                        }

                        if (currentRoom != null) {
                            synchronized (currentRoom) {
                                currentRoom.addPlayer(playerName);
                            }
                        }

                        String response = String.format(
                                "{ \"action\": \"UNICAST\", \"type\": \"ROOM_CREATE_RESPONSE\", \"playerName\": \"%s\", \"roomCode\": \"%s\" }",
                                playerName, roomCode
                        );
                        pw.println(response);
                    }
                    case "JOIN_REQUEST" -> {
                        GameRoom currentRoom;
                        List<String> players;
                        synchronized (gm) {
                            currentRoom = gm.getRoom(code);
                            players = currentRoom != null ? currentRoom.getPlayers() : null;
                        }
                        if (currentRoom != null) {
                            synchronized (currentRoom) {
                                currentRoom.addPlayer(playerName);
                            }
                            String response = String.format(
                                    "{ \"action\": \"BROADCAST\", \"type\": \"JOIN_RESPONSE\", \"playerList\": %s, \"roomCode\": \"%s\" }",
                                    players, code
                            );
                            pw.println(response);
                        } else {
                            String errorResponse = String.format(
                                    "{ \"action\": \"UNICAST\", \"type\": \"ERROR\", \"playerName\": \"%s\", \"message\": \"방이 존재하지 않습니다.\" }",
                                    playerName
                            );
                            pw.println(errorResponse);
                        }
                    }
                    case "START_GAME_REQUEST" -> {
                        GameRoom currentRoom;
                        synchronized (gm) {
                            currentRoom = gm.getRoom(code);
                        }
                        if (currentRoom != null) {
                            String response = currentRoom.getGameController().startGame();
                            pw.println(response);
                        } else {
                            String errorResponse = String.format(
                                    "{ \"action\": \"UNICAST\", \"type\": \"ERROR\", \"playerName\": \"%s\", \"message\": \"방이 존재하지 않습니다.\" }",
                                    playerName
                            );
                            pw.println(errorResponse);
                        }
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
    public static String getRoomCode(String request) {
        return getFieldValue(request, "roomCode", "MISSED_ROOM_CODE", "방 코드가 존재하지 않습니다.");
    }
}
