package com.twilio.trivia.web;

import com.twilio.trivia.model.CreateQuestionRequest;
import com.twilio.trivia.model.Game;
import com.twilio.trivia.model.RealTimeData;
import com.twilio.trivia.model.User;
import com.twilio.trivia.service.TriviaGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TriviaGameController {

    @Autowired
    private TriviaGameService triviaGameService;

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return new ResponseEntity<>(triviaGameService.createUser(user), HttpStatus.OK);
    }

    @PostMapping("/game/add-user/{userId}/{gameId}")
    public ResponseEntity<?> addUserToGame(@PathVariable Long userId, @PathVariable Long gameId) {
        return new ResponseEntity<>(triviaGameService.addUserToGame(userId, gameId), HttpStatus.OK);
    }

    @PostMapping("/start-game")
    public ResponseEntity<Game> startGame() {
        Game game = triviaGameService.startGame();
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @GetMapping("/see-game/{gameId}")
    public ResponseEntity<?> seeGameLiveData(@PathVariable Long gameId) {
        RealTimeData game = triviaGameService.checkGameData(gameId);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @PostMapping("/send-question/")
    public ResponseEntity<?> sendQuestion(@RequestBody CreateQuestionRequest createQuestionRequest) {

        return new ResponseEntity<>(triviaGameService.createQuestion(createQuestionRequest), HttpStatus.OK);
    }

    @PostMapping("/submit-answer/{userId}/{gameId}/{correctAnswer}")
    public ResponseEntity<?> submitAnswer(@PathVariable Long userId,
                                             @PathVariable String gameId,
                                             @PathVariable int correctAnswer) {

        return new ResponseEntity<>(triviaGameService.sendAnswer(userId, gameId, correctAnswer), HttpStatus.OK);
    }

    @PostMapping("/end-game/{gameId}")
    public ResponseEntity<?> endGame(@PathVariable Long gameId) {

        triviaGameService.endGame(gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/send-notification/{gameId}")
    public ResponseEntity<?> concludeGame(@PathVariable String gameId) {
        return new ResponseEntity<>(triviaGameService.sendWinnerNotification(gameId), HttpStatus.OK);
    }
}