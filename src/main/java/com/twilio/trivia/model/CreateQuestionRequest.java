package com.twilio.trivia.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuestionRequest {

    private String questionText;

    private String option1;

    private String option2;

    private String option3;

    private String option4;

    private String gameId;

    private int correctAnswer;

}
