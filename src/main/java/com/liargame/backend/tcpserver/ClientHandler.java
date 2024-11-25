package com.liargame.backend.tcpserver;

import com.liargame.backend.message.*;
import com.liargame.backend.message.base.ErrorResponse;
import com.liargame.backend.message.game.*;
import com.liargame.backend.message.room.CreateRoomRequest;
import com.liargame.backend.message.room.CreateRoomResponse;
import com.liargame.backend.message.room.JoinRequest;
import com.liargame.backend.message.room.JoinResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
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
                logger.info("WebSocket으로부터 요청을 받았습니다: type={}", type);
                switch (type) {
                    case "CREATE_ROOM_REQUEST" -> handleCreateRoom((CreateRoomRequest) request);
                    case "JOIN_REQUEST" -> handleJoinRequest((JoinRequest) request);
                    case "START_GAME_REQUEST" -> handleStartGame((StartGameRequest) request);
                    case "SPEAK_REQUEST" -> handleSpeakTurn((SpeakRequest) request);
                    case "DISCUSS_MESSAGE_REQUEST" -> handleDiscussMessage((DiscussMessageRequest) request);
                    case "VOTE_START_REQUEST" -> handleVoteStart((VoteStartRequest) request);
                    case "VOTE_REQUEST" -> handleVoteRequest((VoteRequest) request);
                    case "GUESS_WORD_REQUEST" -> handleGuessWordRequest((GuessWordRequest) request);
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
    private void handleCreateRoom(CreateRoomRequest request) throws IOException {
        logger.info("방 생성 요청 수신: playerName={}", request.getPlayerName());
        String playerName = request.getPlayerName();
        String roomCode;
        GameRoom currentRoom;
        synchronized (gm) {
            roomCode = gm.createRoom();
            currentRoom = gm.getRoom(roomCode);
        }
        List<String> players = currentRoom.getPlayers();
        if (currentRoom != null) {
            synchronized (currentRoom) {
                currentRoom.addPlayer(playerName);
            }
        }
        CreateRoomResponse response = new CreateRoomResponse(players, playerName, roomCode);
        proxyObjectOutputStream.writeObject(response);
        proxyObjectOutputStream.flush();
    }

    /**
     * JoinRequest 요청을 받고, 응답을 반환해주는 method
     */
    private void handleJoinRequest(JoinRequest request) throws IOException {
        logger.info("방 참여 요청 수신: playerName={}", request.getPlayerName());
        GameRoom currentRoom;
        List<String> players;
        String playerName = request.getPlayerName();
        String code = request.getRoomCode();

        synchronized (gm) {
            currentRoom = gm.getRoom(code);
        }

        if (currentRoom != null) {
            synchronized (currentRoom) {
                players = currentRoom.getPlayers();
                if (players.contains(playerName)) {
                    logger.error("중복된 플레이어 이름입니다: playerName={}", playerName);
                    ErrorResponse response = new ErrorResponse(playerName, "중복된 플레이어 이름입니다.");
                    proxyObjectOutputStream.writeObject(response);
                    proxyObjectOutputStream.flush();
                    return;
                }
                currentRoom.addPlayer(playerName);
                players = currentRoom.getPlayers();
            }
            JoinResponse response = new JoinResponse(players, playerName, code);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        } else {
            logger.error("방이 존재하지 않습니다: roomCode={}", code);
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
        logger.info("게임 시작 요청 수신: roomCode={}", request.getRoomCode());
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
            logger.error("방이 존재하지 않습니다: roomCode={}", code);
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
        logger.info("플레이어 발언 요청 수신: playerName={}, message={}", request.getPlayerName(), request.getMessage());
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
            logger.error("방이 존재하지 않습니다: roomCode={}", code);
            String errorMessage = "방이 존재하지 않습니다.";
            ErrorResponse response = new ErrorResponse(playerName, errorMessage);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        }
    }

    /**
     * DiscussMessageRequest 요청을 받고, 응답을 반환해주는 method
     */
    private void handleDiscussMessage(DiscussMessageRequest request) throws IOException {
        logger.info("플레이어 토론 발언 요청 수신: playerName={}, message={}", request.getPlayerName(), request.getMessage());
        String playerName = request.getPlayerName();
        String message = request.getMessage();
        String code = request.getRoomCode();
        GameRoom currentRoom;
        synchronized (gm) {
            currentRoom = gm.getRoom(code);
        }
        if (currentRoom != null) {
            Message response = currentRoom.getGameController().discuss(playerName, message);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        } else {
            logger.error("방이 존재하지 않습니다: roomCode={}", code);
            String errorMessage = "방이 존재하지 않습니다.";
            ErrorResponse response = new ErrorResponse(playerName, errorMessage);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        }
    }

    /**
     * VoteStartRequest 요청을 받고, 응답을 반환해주는 method
     */
    private void handleVoteStart(VoteStartRequest request) throws IOException {
        logger.info("토론이 끝나고, 투표 시작 요청 수신: roomCode={}", request.getRoomCode());
        String playerName = request.getPlayerName();
        String code = request.getRoomCode();
        GameRoom currentRoom;
        synchronized (gm) {
            currentRoom = gm.getRoom(code);
        }
        if (currentRoom != null) {
            Message response = currentRoom.getGameController().startVote(playerName);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        } else {
            logger.error("방이 존재하지 않습니다: roomCode={}", code);
            String errorMessage = "방이 존재하지 않습니다.";
            ErrorResponse response = new ErrorResponse(playerName, errorMessage);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        }
    }

    private void handleVoteRequest(VoteRequest request) throws IOException {
        String voter = request.getVoter();
        String suspect = request.getSuspect();
        String code = request.getRoomCode();
        GameRoom currentRoom;
        synchronized (gm) {
            currentRoom = gm.getRoom(code);
        }
        if (currentRoom != null) {
            Message response = currentRoom.getGameController().vote(voter, suspect);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        } else {
            logger.error("방이 존재하지 않습니다: roomCode={}", code);
            String errorMessage = "방이 존재하지 않습니다.";
            ErrorResponse response = new ErrorResponse(voter, errorMessage);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        }
    }

    private void handleGuessWordRequest(GuessWordRequest request) throws IOException {
        String playerName = request.getPlayerName();
        String guessWord = request.getGuessWord();
        String code = request.getRoomCode();
        GameRoom currentRoom;
        synchronized (gm) {
            currentRoom = gm.getRoom(code);
        }
        if (currentRoom != null) {
            Message response = currentRoom.getGameController().guess(playerName, guessWord);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        } else {
            logger.error("방이 존재하지 않습니다: roomCode={}", code);
            String errorMessage = "방이 존재하지 않습니다.";
            ErrorResponse response = new ErrorResponse(playerName, errorMessage);
            proxyObjectOutputStream.writeObject(response);
            proxyObjectOutputStream.flush();
        }
    }
}
