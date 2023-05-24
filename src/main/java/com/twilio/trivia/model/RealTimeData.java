package com.twilio.trivia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RealTimeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ElementCollection
    private List<Long> playerIds = new ArrayList<>();

    @ElementCollection
    private Map<Long, Integer> scores = new HashMap<>();

    private int gameProgress = 0;

    private Long gameId;

    private int winnerId = 0;
}
