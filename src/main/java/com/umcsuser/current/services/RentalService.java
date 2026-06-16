package com.umcsuser.current.services;

import com.umcsuser.current.models.Rental;
import com.umcsuser.current.models.User;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.RentalRepository;
import com.umcsuser.current.repositories.UserRepository;
import com.umcsuser.current.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RentalService implements RentalServiceInterface {
    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;

    public RentalService(RentalRepository rentalRepo, VehicleRepository vehicleRepo, UserRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
            throw new IllegalStateException("Vehicle is already rented");
        }

        Rental newRental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .vehicle(vehicle)
                .rentDateTime(LocalDateTime.now().toString())
                .build();

        return rentalRepo.save(newRental);
    }

    @Override
    public Rental returnVehicle(String userId) {
        Optional<Rental> activeRental = findActiveRentalByUserId(userId);
        if (activeRental.isPresent()) {
            Rental rental = activeRental.get();
            rental.setReturnDateTime(LocalDateTime.now().toString());
            return rentalRepo.save(rental);
        }
        throw new IllegalStateException("User has no active rentals");
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()) && r.isActive())
                .findFirst();
    }

    @Override
    public List<Rental> findAllRentals() {
        return rentalRepo.findAll();
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}