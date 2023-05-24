package com.twilio.trivia.service;

import com.twilio.trivia.model.*;
import com.twilio.trivia.repository.GameRepository;
import com.twilio.trivia.repository.QuestionRepository;
import com.twilio.trivia.repository.RealTimeDataRepository;
import com.twilio.trivia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
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

    public String addUserToGame(Long userId, Long gameId) {

        RealTimeData realTimeData = realTimeDataRepository.findByGameId(gameId);
        realTimeData.getPlayerIds().add(userId);
        realTimeData.getScores().put(userId, 0);

        return "User has been successfully added";
    }

    public Question createQuestion(CreateQuestionRequest createQuestionRequest) { // major modification here.

        Question question = new Question();
        question.setQuestionText(createQuestionRequest.getQuestionText());
        question.setGameId(createQuestionRequest.getGameId());
        question.setOption1(createQuestionRequest.getOption1());
        question.setOption2(createQuestionRequest.getOption2());
        question.setOption3(createQuestionRequest.getOption3());
        question.setOption4(createQuestionRequest.getOption4());
        question.setCorrectAnswer(createQuestionRequest.getCorrectAnswer());

        // send question to all game players
        RealTimeData realTimeData = realTimeDataRepository
                .findByGameId(Long.valueOf(createQuestionRequest.getGameId()));

        String message = "Hello there. Your friend has completed an " +
                "EcoTask Challenge. Don't to be a part today!";
        for (Long userId : realTimeData.getPlayerIds()) {
            twilioConfigService.sendMessage("+234" + userId, message);
        }

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

        question.setAnswered(true);

        return "You are correct. Well done!";
    }

    public void endGame(Long gameId) {

        Game game = gameRepository.findById(gameId).orElse(null);
        assert game != null;
        game.setEndTime(LocalDateTime.now());
        game.setOngoing(false);

        gameRepository.save(game);
    }

    public String sendWinnerNotification(String gameId) {

        RealTimeData realTimeData = realTimeDataRepository.findByGameId(Long.valueOf(gameId));
        Map<Long, Integer> realTimeDataScores = realTimeData.getScores();

        String userWithHighestScore = null;
        int highestScore = Integer.MIN_VALUE;

        for (Map.Entry<Long, Integer> entry : realTimeDataScores.entrySet()) {
            Long userId = entry.getKey();
            int score = entry.getValue();

            if (score > highestScore) {
                highestScore = score;
                userWithHighestScore = userRepository.findById(userId).get().getName();;
            }
        }

        // more modifications
        String message = "Hello there. Your friend %s has won the tiriva. Congratulations to them. Thanks for playing";
        for (Long userId : realTimeData.getPlayerIds()) {
            twilioConfigService.sendMessage("+234" + userId, message);
        }
        return "Notification sent";
    }

}
