package com.twilio.trivia.web;

import com.twilio.trivia.model.Game;
import com.twilio.trivia.model.Question;
import com.twilio.trivia.model.User;
import com.twilio.trivia.service.TriviaGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TriviaGameController {

    @Autowired
    private TriviaGameService triviaGameService;

    @PostMapping("/start-game")
    public ResponseEntity<Game> startGame() {
        Game game = triviaGameService.startGame();
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @PostMapping("/send-question/{gameId}/{questionId}")
    public ResponseEntity<Void> sendQuestion(@PathVariable Long gameId, @PathVariable Long questionId) {
        Game game = new Game();
        game.setId(gameId);
        Question question = new Question();
        question.setId(questionId);
        triviaGameService.sendQuestion(game, question);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/submit-answer/{userId}/{questionId}/{answer}")
    public ResponseEntity<Void> submitAnswer(@PathVariable Long userId, @PathVariable Long questionId, @PathVariable String answer) {
        User user = new User();
        user.setId(userId);
        Question question = new Question();
        question.setId(questionId);
        triviaGameService.submitAnswer(user, question, answer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/end-game/{gameId}")
    public ResponseEntity<Void> endGame(@PathVariable Long gameId) {
        Game game = new Game();
        game.setId(gameId);
        triviaGameService.endGame(game);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

