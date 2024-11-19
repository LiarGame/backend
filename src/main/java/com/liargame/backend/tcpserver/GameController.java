package com.liargame.backend.tcpserver;

import com.liargame.backend.message.base.ErrorResponse;
import com.liargame.backend.message.Message;
import com.liargame.backend.message.game.DiscussStartResponse;
import com.liargame.backend.message.game.RoleAssignResponse;
import com.liargame.backend.message.game.SpeakResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameRoom gameRoom;
    private boolean startFlag;

    public GameController(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public synchronized Message startGame(String playerName) {
        startFlag = !startFlag;
        if (!startFlag) {
            logger.error("이미 게임이 진행중인 게임방입니다: roomCode={}", gameRoom.getRoomCode());
            String errorMessage = "이미 게임이 진행중인 게임방입니다.";
            return new ErrorResponse(playerName, errorMessage);
        }
        List<String> players = gameRoom.getPlayers();
        if (players.size() < 3) {
            logger.error("게임에 참여한 플레이어는 세 명 이상이어야 합니다: players={}", players);
            String errorMessage = "게임에 참여한 플레이어는 세 명 이상이어야 합니다.";
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
        int speakCount = gameRoom.getSpeakCount();

        // 발언 순서가 아닌 플레이어가 발언을 하면 오류 메시지 반환
        String expectedPlayer = players.get(speakCount % players.size());
        if (!expectedPlayer.equals(playerName)) {
            logger.error("현재 발언할 순서가 아닙니다: playerName={}", playerName);
            String errorMessage = "현재 발언할 순서가 아닙니다.";
            return new ErrorResponse(playerName, errorMessage);
        }
        gameRoom.incrementSpeakCount();

        // 모든 플레이어가 두 번씩 발언하면 토론 시작
        if (speakCount == players.size() * 2) {
            logger.info("모든 플레이어가 두 번씩 발언을 진행하고, 토론을 시작합니다.");
            return new DiscussStartResponse(gameRoom.getRoomCode());
        }

        // 현재 발언자의 인덱스 get
        int currentPlayerAt = players.indexOf(playerName);
        String nextPlayer = currentPlayerAt == players.size() - 1 ? players.get(0) : players.get(currentPlayerAt + 1);
        return new SpeakResponse(playerName, message, nextPlayer);
    }
    // TODO: startFlag 다시 false로 돌리는 로직 필요
    public void endGame() {
        startFlag = false;
    }
}
