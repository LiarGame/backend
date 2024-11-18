package com.liargame.backend.tcpserver;

import com.liargame.backend.message.*;
import com.liargame.backend.message.base.ErrorResponse;
import com.liargame.backend.message.game.SpeakRequest;
import com.liargame.backend.message.game.StartGameRequest;
import com.liargame.backend.message.room.JoinRequest;
import com.liargame.backend.message.room.JoinResponse;
import com.liargame.backend.message.room.RoomCreateRequest;
import com.liargame.backend.message.room.RoomCreateResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private ObjectInputStream proxyObjectInputStream;
    private ObjectOutputStream proxyObjectOutputStream;
    private final GameRoomManager gm;

    public ClientHandler(Socket socket, GameRoomManager gm) {
        this.socket = socket;
        this.gm = gm;
    }

    @Override
    public void run() {
        try {
            proxyObjectInputStream = new ObjectInputStream(socket.getInputStream());
            proxyObjectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            Message request;
            while ((request = (Message) proxyObjectInputStream.readObject()) != null) {
                String type = request.getType();
                System.out.println("요청: " + type);
                switch (type) {
                    case "CREATE_ROOM_REQUEST" -> handleCreateRoom((RoomCreateRequest) request);
                    case "JOIN_REQUEST" -> handleJoinRequest((JoinRequest) request);
                    case "START_GAME_REQUEST" -> handleStartGame((StartGameRequest) request);
                    case "SPEAK_REQUEST" -> handleSpeakTurn((SpeakRequest) request);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (proxyObjectInputStream != null) proxyObjectInputStream.close();
                if (proxyObjectOutputStream != null) proxyObjectOutputStream.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * CreateRoomRequest 요청을 받고, 응답을 반환해주는 method
     */
    private void handleCreateRoom(RoomCreateRequest request) throws IOException {
        String playerName = request.getPlayerName();
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
        RoomCreateResponse response = new RoomCreateResponse(playerName, roomCode);
        proxyObjectOutputStream.writeObject(response);
        proxyObjectOutputStream.flush();
    }

    /**
     * JoinRequest 요청을 받고, 응답을 반환해주는 method
     */
    private void handleJoinRequest(JoinRequest request) throws IOException {
        GameRoom currentRoom;
        List<String> players;
        String playerName = request.getPlayerName();
        String code = request.getRoomCode();
        synchronized (gm) {
            currentRoom = gm.getRoom(code);
            players = currentRoom != null ? currentRoom.getPlayers() : null;
        }
        if (currentRoom != null) {
            synchronized (currentRoom) {
                currentRoom.addPlayer(playerName);
            }
            JoinResponse response = new JoinResponse(players, code);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        } else {
            String errorMessage = "방이 존재하지 않습니다.";
            ErrorResponse response = new ErrorResponse(playerName, errorMessage);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        }
    }

    /**
     * StartGameRequest 요청을 받고, 응답을 반환해주는 method
     */
    private void handleStartGame(StartGameRequest request) throws IOException {
        String playerName = request.getPlayerName();
        String code = request.getRoomCode();
        GameRoom currentRoom;
        synchronized (gm) {
            currentRoom = gm.getRoom(code);
        }
        if (currentRoom != null) {
            Message response = currentRoom.getGameController().startGame(playerName);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        } else {
            String errorMessage = "방이 존재하지 않습니다.";
            ErrorResponse response = new ErrorResponse(playerName, errorMessage);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        }
    }

    /**
     * SpeakRequest 요청을 받고, 응답을 반환해주는 method
     */
    private void handleSpeakTurn(SpeakRequest request) throws IOException {
        String playerName = request.getPlayerName();
        String message = request.getMessage();
        String code = request.getRoomCode();
        GameRoom currentRoom;
        synchronized (gm) {
            currentRoom = gm.getRoom(code);
        }
        if (currentRoom != null) {
            Message response = currentRoom.getGameController().speakTurn(playerName, message);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        } else {
            String errorMessage = "방이 존재하지 않습니다.";
            ErrorResponse response = new ErrorResponse(playerName, errorMessage);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        }
    }
}
