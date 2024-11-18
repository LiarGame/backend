package com.liargame.backend.tcpserver;

import com.liargame.backend.message.base.ErrorResponse;
import com.liargame.backend.message.Message;
import com.liargame.backend.message.game.DiscussStartResponse;
import com.liargame.backend.message.game.RoleAssignResponse;
import com.liargame.backend.message.game.SpeakResponse;

import java.util.Collections;
import java.util.List;

public class GameController {
    private final GameRoom gameRoom;
    private int speakCount;

    public GameController(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public synchronized Message startGame(String playerName) {
        List<String> players = gameRoom.getPlayers();
        if (players.size() < 2) {
            String errorMessage = "게임에 참여한 플레이어는 두 명 이상이어야 합니다.";
            return new ErrorResponse(playerName, errorMessage);
        }

        Collections.shuffle(players);
        String liar = players.get(0);
        TopicEnum topic = TopicEnum.getRandomTopic();
        String word = topic.getRandomWord();

        return new RoleAssignResponse(liar, topic, word, gameRoom.getRoomCode());
    }

    public synchronized Message speakTurn(String playerName, String message) {
        List<String> players = gameRoom.getPlayers();

        // 발언 순서가 아닌 플레이어가 발언을 하면 오류 메시지 반환
        String expectedPlayer = players.get(speakCount % players.size());
        if (!expectedPlayer.equals(playerName)) {
            String errorMessage = "현재 발언할 순서가 아닙니다.";
            return new ErrorResponse(playerName, errorMessage);
        }

        speakCount++;

        // 모든 플레이어가 두 번씩 발언하면 토론 시작
        if (speakCount == players.size() * 2) {
            return new DiscussStartResponse(gameRoom.getRoomCode());
        }

        // 현재 발언자의 인덱스 get
        int currentPlayerAt = players.indexOf(playerName);
        if (currentPlayerAt == -1) {
            String errorMessage = "플레이어가 존재하지 않습니다.";
            return new ErrorResponse(playerName, errorMessage);
        }
        String nextPlayer;

        // 리스트의 마지막 플레이어인 경우 다음 플레이어는 리스트의 첫 번째 플레이어
        if (currentPlayerAt == players.size() - 1) {
            nextPlayer = players.get(0);
        } else {
            nextPlayer = players.get(currentPlayerAt + 1);
        }
        return new SpeakResponse(playerName, message, nextPlayer);
    }
}
