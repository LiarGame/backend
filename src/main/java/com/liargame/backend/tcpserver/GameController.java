package com.liargame.backend.tcpserver;

import com.liargame.backend.message.base.ErrorResponse;
import com.liargame.backend.message.Message;
import com.liargame.backend.message.game.RoleAssignResponse;

import java.util.Collections;
import java.util.List;

public class GameController {
    private final GameRoom gameRoom;


    public GameController(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public synchronized Message startGame(String playerName) {
        List<String> players = gameRoom.getPlayers();
        if (players.size() < 2) {
            String message = "게임에 참여한 플레이어는 두 명 이상이어야 합니다.";
            return new ErrorResponse(playerName, message);
        }

        Collections.shuffle(players);
        String liar = players.get(0);
        TopicEnum topic = TopicEnum.getRandomTopic();
        String word = topic.getRandomWord();

        return new RoleAssignResponse(liar, topic, word, gameRoom.getRoomCode());
    }
}
