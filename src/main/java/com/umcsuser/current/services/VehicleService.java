package com.umcsuser.current.services;

import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.RentalRepository;
import com.umcsuser.current.repositories.VehicleRepository;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleService {
    private final VehicleRepository vehicleRepo;
    private final RentalRepository rentalRepo;

    public VehicleService(VehicleRepository vehicleRepo, RentalRepository rentalRepo) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
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
        vehicleRepo.save(vehicle);
        return true;
    }

    public boolean removeVehicle(String id) {
        if (rentalRepo.findByVehicleIdAndReturnDateIsNull(id).isPresent()) {
            return false;
        }
        vehicleRepo.deleteById(id);
        return true;
    }
}