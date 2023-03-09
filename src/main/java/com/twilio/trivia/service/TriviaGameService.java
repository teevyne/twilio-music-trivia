package com.twilio.trivia.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.trivia.model.*;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TriviaGameService {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private GameService gameService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private RealTimeDataService realTimeDataService;

    @Autowired
    private TwilioConfig twilioConfig;

    @Value("${twilio.from.number}")
    private String twilioFromNumber;

    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    public Game startGame() {
        Game game = new Game();
        game.setStartTime(LocalDateTime.now());
        game = gameService.saveGame(game);
        return game;
    }

    public void sendQuestion(Game game, Question question) {

        List<String> answers = Arrays.asList(question.getOption1(), question.getOption2(), question.getOption3(), question.getOption4());
        Collections.shuffle(answers);

        List<User> usersList= userService.getAllUsers();

        StringBuilder sb = new StringBuilder();
        sb.append(question.getQuestionText()).append("\n");
        for (int i = 0; i < answers.size(); i++) {
            sb.append(i+1).append(". ").append(answers.get(i)).append("\n");
        }

        for (User user : usersList) {
            sendMessage(user.getPhoneNumber(), sb.toString());
        }
    }

    public void submitAnswer(User user, Question question, String answer) {
        Score score = new Score();
        score.setUser(user);
        score.setGame(question.getGame());
        score.setScore(0);
        if (answer.equals(question.getCorrectAnswer())) {
            score.setScore(1);
        }
        score = scoreService.saveScore(score);
        updateRealTimeData(question.getGame(), user, score.getScore());
    }

    public void endGame(Game game) {
        game.setEndTime(LocalDateTime.now());
        gameService.saveGame(game);
    }

    private void updateRealTimeData(Game game, User user, int score) {
        RealTimeData realTimeData = realTimeDataService.getRealTimeDataById(game.getId());
        if (realTimeData == null) {
            realTimeData = new RealTimeData();
            realTimeData.setGame(game);
        }
        Map<Long, Integer> scores = realTimeData.getScores();
        if (scores == null) {
            scores = new HashMap<>();
        }
        scores.put(user.getId(), score);
        realTimeData.setScores(scores);
        realTimeDataService.saveRealTimeData(realTimeData);
    }

    private void sendMessage(String to, String message) {
        Twilio.init(twilioConfig.twilioRestClient().getAccountSid(), twilioAuthToken);
        Message.creator(new PhoneNumber(to), new PhoneNumber(twilioFromNumber), message).create();
    }
}
