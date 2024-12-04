package com.liargame.backend.tcpserver;

import com.liargame.backend.message.base.ErrorResponse;
import com.liargame.backend.message.Message;
import com.liargame.backend.message.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameRoom gameRoom;
    private boolean startFlag;
    private final Map<String, Integer> numOfVotes;

    public GameController(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
        this.numOfVotes = new HashMap<>();
    }

    public synchronized Message startGame(String playerName) {
        if (startFlag) {
            logger.error("이미 게임이 진행중인 게임방입니다: roomCode={}", gameRoom.getRoomCode());
            return new ErrorResponse(playerName, "이미 게임이 진행중인 게임방입니다.");
        }
        startFlag = true;
        numOfVotes.clear();

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

        gameRoom.setGameDetails(liar, topic, word);

        return new RoleAssignResponse(players, liar, topic, word, gameRoom.getRoomCode());
    }

    public synchronized Message speakTurn(String playerName, String message) {
        List<String> players = gameRoom.getPlayers();
        int speakCount = gameRoom.getSpeakCount();

        // 발언 순서가 아닌 플레이어가 발언을 하면 오류 메시지 반환
        String expectedPlayer = players.get(speakCount % players.size());
        if (!expectedPlayer.equals(playerName)) {
            logger.error("현재 발언할 순서가 아닙니다: playerName={}", playerName);
            return new ErrorResponse(playerName, "현재 발언할 순서가 아닙니다.");
        }
        gameRoom.incrementSpeakCount();
        int updatedSpeakCount = gameRoom.getSpeakCount();
        logger.info("updatedSpeakCount={}", updatedSpeakCount);

        int currentPlayerAt = players.indexOf(playerName);
        String nextPlayer = currentPlayerAt == players.size() - 1 ? players.get(0) : players.get(currentPlayerAt + 1);
        SpeakResponse speakResponse = new SpeakResponse(players, playerName, message, nextPlayer, gameRoom.getRoomCode(), false);

        if (updatedSpeakCount == players.size()) {
            logger.info("모든 플레이어가 두 번씩 발언을 완료했습니다. 토론을 시작합니다.");
            speakResponse = new SpeakResponse(players, playerName, message, nextPlayer, gameRoom.getRoomCode(), true);
        }
        return speakResponse;
    }

    public Message discuss(String playerName, String message) {
        if (!gameRoom.getPlayers().contains(playerName)) {
            logger.error("플레이어가 방에 속해있지 않습니다: playerName={}", playerName);
            String errorMessage = "플레이어가 방에 속해있지 않습니다.";
            return new ErrorResponse(playerName, errorMessage);
        }
        logger.info("플레이어가 토론에서 발언을 합니다: playerName={}, message={}", playerName, message);
        return new DiscussMessageResponse(playerName, message, gameRoom.getRoomCode());
    }

    public Message startVote(String playerName) {
        String code = gameRoom.getRoomCode();
        logger.info("토론이 끝나고 투표를 시작합니다: roomCode={}", code);
        if (!startFlag) {
            logger.error("게임을 진행중인 방이 아닙니다: roomCode={}", code);
            String errorMessage = "게임을 진행중인 방이 아닙니다.";
            return new ErrorResponse(playerName, errorMessage);
        }
        return new VoteStartResponse(code);
    }

    public synchronized Message vote(String voter, String suspect) {
        String code = gameRoom.getRoomCode();
        List<String> players = gameRoom.getPlayers();
        if (!startFlag) {
            logger.error("게임을 진행중인 방이 아닙니다: roomCode={}", code);
            String errorMessage = "게임을 진행중인 방이 아닙니다.";
            return new ErrorResponse(voter, errorMessage);
        }
        Set<String> votedPlayer = gameRoom.getVotedPlayer();
        if (!gameRoom.addVotedPlayer(voter)) {
            logger.error("이미 투표를 진행한 플레이어입니다: voter={}", voter);
            String errorMessage = "이미 투표를 진행한 플레이어입니다.";
            return new ErrorResponse(voter, errorMessage);
        }
        logger.info("플레이어가 투표를 합니다: voter={}, suspect={}", voter, suspect);
        numOfVotes.merge(suspect, 1, Integer::sum);

        if (votedPlayer.size() == gameRoom.getPlayers().size()) {
            logger.info("모든 플레이어가 투표를 했습니다");

            String mostVotedPlayer = numOfVotes.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
            String liar = gameRoom.getLiar();
            boolean isLiarCaught = mostVotedPlayer != null && mostVotedPlayer.equals(liar);
            if (!isLiarCaught) {
                List<String> playersWithoutLiar = new ArrayList<>(gameRoom.getPlayers());
                playersWithoutLiar.remove(liar);
                gameRoom.updatePlayerScore(liar, 3);
                liar = gameRoom.getLiar();
                endGame();
                return new GameResultResponse(List.of(liar), playersWithoutLiar, List.of(liar), gameRoom.getTopic(), gameRoom.getWord(), code);
            }
            logger.info("투표 결과: mostVotedPlayer={}, liar={}, isLiarCaught={}", mostVotedPlayer, liar, isLiarCaught);
            return new VoteResult(isLiarCaught, liar, code);
        }

        return new VoteResponse(players, voter, suspect, code);
    }
    public Message guess(String playerName, String guessWord) {
        String code = gameRoom.getRoomCode();

        if (!startFlag) {
            logger.error("게임을 진행중인 방이 아닙니다: roomCode={}", code);
            String errorMessage = "게임을 진행중인 방이 아닙니다.";
            return new ErrorResponse(playerName, errorMessage);
        }

        if (!playerName.equals(gameRoom.getLiar())) {
            logger.error("라이어가 아닙니다: playerName={}, liar={}", playerName, gameRoom.getLiar());
            return new ErrorResponse(playerName, "라이어가 아닙니다.");
        }

        TopicEnum topic = gameRoom.getTopic();
        String word = gameRoom.getWord();
        boolean isGuessCorrect = guessWord.equals(word);
        String liar = gameRoom.getLiar();

        List<String> playersWithoutLiar = new ArrayList<>(gameRoom.getPlayers());
        playersWithoutLiar.remove(liar); // 시민팀 리스트 생성

        // 점수 업데이트 로직
        if (isGuessCorrect) {
            // 라이어 정답 추측 성공 -> 라이어 +3점
            gameRoom.updatePlayerScore(liar, 3);
            liar = gameRoom.getLiar();
            logger.info("라이어가 정답을 맞혔습니다: playerName={}, scoreUpdated=+3", liar);
        } else {
            // 라이어 정답 추측 실패 -> 시민팀 각각 +1점
            for (String citizen : playersWithoutLiar) {
                gameRoom.updatePlayerScore(citizen, 1);
                playersWithoutLiar = gameRoom.getPlayers();
                playersWithoutLiar.remove(liar);
                logger.info("시민에게 점수 추가: citizen={}, scoreUpdated=+1", citizen);
            }
        }

        List<String> winner = isGuessCorrect
                ? Collections.singletonList(gameRoom.getLiar())
                : playersWithoutLiar;

        endGame();

        logger.info("라이어가 단어를 추측했습니다: playerName={}, guessWord={}, isGuessCorrect={}, winner={}",
                playerName, guessWord, isGuessCorrect, winner);

        return new GameResultResponse(
                winner,
                playersWithoutLiar,
                List.of(liar),
                topic,
                word,
                code
        );
    }

    public Message restartGame() {
        List<String> players = gameRoom.getPlayers();
        return new RestartRoomResponse(players, gameRoom.getRoomCode());
    }

    public boolean isAllPlayersSpokeTwice() {
        List<String> players = gameRoom.getPlayers();
        return gameRoom.getSpeakCount() >= players.size() * 2;
    }

    public void endGame() {
        startFlag = false;
        numOfVotes.clear();
        gameRoom.resetGameRoomState();
    }
}
