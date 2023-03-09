package com.twilio.trivia.service;

import com.twilio.trivia.model.Score;
import com.twilio.trivia.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    public List<Score> getAllScores() {
        return scoreRepository.findAll();
    }

    public Score getScoreById(Long id) {
        return scoreRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Score not found with id " + id));
    }

    public Score saveScore(Score score) {
        return scoreRepository.save(score);
    }

    public void deleteScoreById(Long id) {
        scoreRepository.deleteById(id);
    }
}