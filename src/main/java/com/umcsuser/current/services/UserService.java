package com.umcsuser.current.services;

import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.RentalRepository;
import com.umcsuser.current.repositories.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;

    public UserService(UserRepository userRepo, RentalRepository rentalRepo) {
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
    }

    public void removeUser(String userId) {
        boolean hasActiveRentals = rentalRepo.findAll().stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.isActive());

        if (hasActiveRentals) {
            throw new IllegalStateException("User currently has a rented vehicle.");
        }

        userRepo.deleteById(userId);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}