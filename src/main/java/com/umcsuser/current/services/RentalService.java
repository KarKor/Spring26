package com.umcsuser.current.services;

import com.umcsuser.current.models.Rental;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.RentalRepository;
import com.umcsuser.current.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RentalService {
    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;

    public RentalService(RentalRepository rentalRepo, VehicleRepository vehicleRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
    }

    public boolean rentVehicle(String userId, String vehicleId) {
        if (vehicleRepo.findById(vehicleId).isEmpty()) return false;

        if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
            return false;
        }

        Rental newRental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .vehicleId(vehicleId)
                .rentDateTime(LocalDateTime.now().toString())
                .build();

        rentalRepo.save(newRental);
        return true;
    }

    public boolean returnVehicle(String userId, String vehicleId) {
        Optional<Rental> activeRental = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId);

        if (activeRental.isPresent() && activeRental.get().getUserId().equals(userId)) {
            Rental rental = activeRental.get();
            rental.setReturnDateTime(LocalDateTime.now().toString());
            rentalRepo.save(rental);
            return true;
        }
        return false;
    }

    public List<Vehicle> getUserActiveRentals(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .map(r -> vehicleRepo.findById(r.getVehicleId()).orElse(null))
                .filter(v -> v != null)
                .collect(Collectors.toList());
    }
}