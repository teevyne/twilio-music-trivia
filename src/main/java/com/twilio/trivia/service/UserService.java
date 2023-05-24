//package com.twilio.trivia.service;
//
//import com.twilio.trivia.model.User;
//import com.twilio.trivia.repository.UserRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public User getUserById(Long id) {
//        return userRepository.findById(id).orElseThrow(() ->
//                new EntityNotFoundException("User not found with id " + id));
//    }
//
//}