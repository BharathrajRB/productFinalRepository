package com.example.productmanagement.service;

import com.example.productmanagement.modal.User;
import com.example.productmanagement.repository.UserRepository;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user) {
        validateUser(user);
        userRepository.save(user);
    }

    private void validateUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }
    }

    public boolean authenticateUser(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        return user != null;
    }

    public String encodeCredentials(String email, String password) {
        String credentials = email + ":" + password;
        String result = Base64.getEncoder().encodeToString(credentials.getBytes());

        return result;
    }

    public User findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    public User getUserByemail(String email) {
        return userRepository.findByEmail(email);
    }

}