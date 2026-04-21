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
    private final VehicleValidator vehicleValidator; // Nowy walidator

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
                .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .collect(Collectors.toList());
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (vehicleRepo.findById(vehicle.getId()).isPresent()) {
            return false;
        }
        vehicleValidator.validate(vehicle);

        vehicleRepo.save(vehicle);
        return true;
    }

    public boolean removeVehicle(String id) {
        if (rentalRepo.findByVehicleIdAndReturnDateIsNull(id).isPresent()) {
            throw new IllegalStateException("Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }
        vehicleRepo.deleteById(id);
        return true;
    }
}