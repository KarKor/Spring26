package com.umcsuser.current.services;

import com.umcsuser.current.models.Role;
import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLogin(login);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return user.copy();
            }
        }
        return null;
    }

    public User register(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            System.out.println("This login is already in use.");
            return null;
        }

        Role assignedRole = userRepository.findAll().isEmpty() ? Role.ADMIN : Role.USER;

        String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = User.builder()
                .ID(UUID.randomUUID().toString())
                .login(login)
                .passwordHash(hashedPw)
                .role(assignedRole)
                .build();

        return userRepository.save(newUser);
    }
}