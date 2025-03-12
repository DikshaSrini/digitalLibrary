package com.example.library.service;

import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(String username, String password, String role) {
        User user = new User(username, password, role);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();  // Fetches all users from MongoDB
    }

    public void deleteUserById(String id) {
        userRepository.deleteById(id);  // Allows deleting users by ID
    }
}
