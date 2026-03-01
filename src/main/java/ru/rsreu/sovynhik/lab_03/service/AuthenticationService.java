package ru.rsreu.sovynhik.lab_03.service;

import ru.rsreu.sovynhik.lab_03.model.User;
import ru.rsreu.sovynhik.lab_03.repository.UserRepository;
import java.util.Optional;

public class AuthenticationService {
    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByName(username.trim());
    }

    public boolean isUserExists(String username) {
        return userRepository.exists(username);
    }
}