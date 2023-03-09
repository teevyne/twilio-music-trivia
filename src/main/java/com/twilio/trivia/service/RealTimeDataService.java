package com.twilio.trivia.service;

import com.twilio.trivia.model.RealTimeData;
import com.twilio.trivia.repository.RealTimeDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class RealTimeDataService {

    @Autowired
    private RealTimeDataRepository realTimeDataRepository;

    public List<RealTimeData> getAllRealTimeData() {
        return realTimeDataRepository.findAll();
    }

    public RealTimeData getRealTimeDataById(Long id) {
        return realTimeDataRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Real-time data not found with id " + id));
    }

    public void saveRealTimeData(RealTimeData realTimeData) {
        realTimeDataRepository.save(realTimeData);
    }

    public void deleteRealTimeDataById(Long id) {
        realTimeDataRepository.deleteById(id);
    }
}

