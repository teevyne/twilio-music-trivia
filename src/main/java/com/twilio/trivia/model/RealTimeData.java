package com.twilio.trivia.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private Long gameId;

    private List<Long> playerIds;

    private Map<Long, Integer> scores;

    private int gameProgress;

    @OneToOne
    @JoinColumn(name = "game_id")
    private Game game;
}
