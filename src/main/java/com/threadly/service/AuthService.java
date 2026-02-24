package com.threadly.service;

import com.threadly.dto.RegisterRequest;
import com.threadly.entity.User;
import com.threadly.exception.ApiException;
import com.threadly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest registerRequest) {

        if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ApiException("Username is already in use");
        }
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ApiException("Email is already in use");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setProvider("LOCAL");

        userRepository.save(user);
    }
}
