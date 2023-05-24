package com.twilio.trivia.repository;

import com.twilio.trivia.model.RealTimeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealTimeDataRepository extends JpaRepository<RealTimeData, Long> {

    RealTimeData findByGameId(Long gameId);

}
