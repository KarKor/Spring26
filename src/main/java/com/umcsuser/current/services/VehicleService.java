package com.umcsuser.current.services;

import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.RentalRepository;
import com.umcsuser.current.repositories.VehicleRepository;
import com.umcsuser.current.services.VehicleValidator;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleService {
    private final VehicleRepository vehicleRepo;
    private final RentalRepository rentalRepo;
    private final VehicleValidator vehicleValidator;

    public VehicleService(VehicleRepository vehicleRepo, RentalRepository rentalRepo, VehicleValidator vehicleValidator) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
        this.vehicleValidator = vehicleValidator;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepo.findAll();
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepo.findAll().stream()
                .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull((String) v.getId()).isEmpty())
                .collect(Collectors.toList());
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getId() == null) {
            throw new IllegalArgumentException("Vehicle or its ID cannot be null");
        }
        try {
            if (vehicleRepo.findById((String) vehicle.getId()).isPresent()) {
                return false;
            }
        } catch (NullPointerException e) {
            System.out.println("Repository error: findById() returned null");
            return false;
        }

        vehicleValidator.validate(vehicle);
        vehicleRepo.save(vehicle);
        return true;
    }

    public boolean removeVehicle(String id) {
        if (rentalRepo.findByVehicleIdAndReturnDateIsNull(id).isPresent()) {
            throw new IllegalStateException("Cannot remove a rented vehicle.");
        }
        vehicleRepo.deleteById(id);
        return true;
    }
}