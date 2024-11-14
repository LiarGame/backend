package com.liargame.backend.tcpserver;

import java.util.Collections;
import java.util.List;

public class GameController {
    private final GameRoom gameRoom;


    public GameController(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public String startGame() {
        String word;
        String liar;
        TopicEnum topic;
        List<String> players = gameRoom.getPlayers();
        if (players.isEmpty()) {
            return "{ \"action\": \"UNICAST\", \"type\": \"ERROR\", \"message\": \"플레이어가 없습니다.\" }";
        }

        Collections.shuffle(players);
        liar = players.get(0);

        topic = TopicEnum.getRandomTopic();
        word = topic.getRandomWord();

        return String.format(
                "{ \"action\": \"BROADCAST\", \"type\": \"ROLE_ASSIGN_RESPONSE\", \"liar\": \"%s\", \"topic\": \"%s\", \"word\": \"%s\", \"roomCode\": \"%s\" }",
                liar, topic.getTopic(), word, gameRoom.getRoomCode()
        );
    }
}
