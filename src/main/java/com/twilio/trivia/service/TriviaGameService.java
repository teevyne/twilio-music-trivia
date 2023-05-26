package com.twilio.trivia.service;

import com.twilio.trivia.model.*;
import com.twilio.trivia.repository.GameRepository;
import com.twilio.trivia.repository.QuestionRepository;
import com.twilio.trivia.repository.RealTimeDataRepository;
import com.twilio.trivia.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class TriviaGameService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RealTimeDataRepository realTimeDataRepository;

    @Autowired
    private TwilioConfigService twilioConfigService;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Game startGame() {
        Game game = new Game();
        game.setStartTime(LocalDateTime.now());
        game = gameRepository.save(game);

        RealTimeData realTimeData = new RealTimeData();
        realTimeData.setGameId(game.getId());
        realTimeDataRepository.save(realTimeData);

        return game;
    }

    public RealTimeData checkGameData(Long gameId) {
        return realTimeDataRepository.findByGameId(gameId);
    }

    public String addUserToGame(Long userId, Long gameId) {

        RealTimeData realTimeData = realTimeDataRepository.findByGameId(gameId);
        realTimeData.getPlayerIds().add(userId);
        realTimeData.getScores().put(userId, 0);
        realTimeDataRepository.save(realTimeData);

        return "User has been successfully added";
    }

    public Question createQuestion(CreateQuestionRequest createQuestionRequest) { // major modification here.

        Question question = new Question();
        question.setQuestionText(createQuestionRequest.getQuestionText());
        question.setCorrectAnswer(createQuestionRequest.getCorrectAnswer());
        question.setGameId(createQuestionRequest.getGameId());

        // send question to all game players
        RealTimeData realTimeData = realTimeDataRepository
                .findByGameId(Long.valueOf(createQuestionRequest.getGameId()));

        for (Long userId : realTimeData.getPlayerIds()) {
            String userPhoneNumber = userRepository.findById(userId).get().getPhoneNumber();
            twilioConfigService.sendMessage("+234" + userPhoneNumber.substring(1),
                    createQuestionRequest.getQuestionText());
        }

        log.info(String.format("message sent: %s", createQuestionRequest.getQuestionText()));

        return questionRepository.save(question);
    }

    public String sendAnswer(Long userId, String gameId, int correctAnswer) {

        Question question = questionRepository.findByGameId(gameId);

        if (question.getCorrectAnswer() != correctAnswer) {
            return "Wrong answer. Thank you for trying";
        }

        RealTimeData realTimeData = realTimeDataRepository.findByGameId(Long.valueOf(gameId));

        int currentScore = realTimeData.getScores().get(userId);
        int newScore = currentScore + 1;
        realTimeData.getScores().put(userId, newScore);
        realTimeDataRepository.save(realTimeData);

        return "You are correct. Well done!";
    }

    public void endGame(Long gameId) {

        Game game = gameRepository.findById(gameId).orElse(null);
        assert game != null;
        game.setEndTime(LocalDateTime.now());
        game.setOngoing(false);

        gameRepository.save(game);
    }
    String userWithHighestScore = null;

    public String sendWinnerNotification(String gameId) {
        Long winnerId = null;

        RealTimeData realTimeData = realTimeDataRepository.findByGameId(Long.valueOf(gameId));
        Map<Long, Integer> realTimeDataScores = realTimeData.getScores();

        int highestScore = Integer.MIN_VALUE;

        for (Map.Entry<Long, Integer> entry : realTimeDataScores.entrySet()) {
            Long userId = entry.getKey();
            int score = entry.getValue();

            if (score > highestScore) {
                highestScore = score;
                userWithHighestScore = userRepository.findById(userId).get().getName();
                winnerId = userId;
            }
        }

        // more modifications
        String message = String.format("Hello there. Player with ID %s has won the trivia. Congratulations to " +
                "them. Thanks for playing", winnerId);

        for (Long userId : realTimeData.getPlayerIds()) {
            String userPhoneNumber = userRepository.findById(userId).get().getPhoneNumber();
            twilioConfigService.sendMessage("+234" + userPhoneNumber.substring(1), message);
        }
        log.info(message);

        return "Notification sent";
    }

}
