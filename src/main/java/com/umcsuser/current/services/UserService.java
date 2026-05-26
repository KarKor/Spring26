package com.umcsuser.current.services;

import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.RentalRepository;
import com.umcsuser.current.repositories.UserRepository;

import java.util.List;

public class UserService implements UserServiceInterface {
    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;

    public UserService(UserRepository userRepo, RentalRepository rentalRepo) {
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User findById(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        if (id.equals(loggedUserId)) {
            throw new IllegalStateException("Nie możesz usunąć własnego konta.");
        }

        boolean hasActiveRentals = rentalRepo.findAll().stream()
                .anyMatch(r -> r.getUserId().equals(id) && r.isActive());

        if (hasActiveRentals) {
            throw new IllegalStateException("User currently has a rented vehicle.");
        }

        userRepo.deleteById(id);
    }
}