package com.twilio.trivia.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuestionRequest {

    private String questionText;

    private String gameId;

    private int correctAnswer;

}
