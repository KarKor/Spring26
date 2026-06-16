package com.umcsuser.current.services;

import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.RentalRepository;
import com.umcsuser.current.repositories.VehicleRepository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VehicleService implements VehicleServiceInterface {
    private final VehicleRepository vehicleRepo;
    private final RentalRepository rentalRepo;
    private final VehicleValidator vehicleValidator;

    public VehicleService(VehicleRepository vehicleRepo, RentalRepository rentalRepo, VehicleValidator vehicleValidator) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
        this.vehicleValidator = vehicleValidator;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        return vehicleRepo.findAll();
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepo.findAll().stream()
                .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public Vehicle findById(String id) {
        return vehicleRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu"));
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getId() == null) {
            throw new IllegalArgumentException("Vehicle or its ID cannot be null");
        }
        if (vehicleRepo.findById(vehicle.getId()).isPresent()) {
            return null;
        }

        vehicleValidator.validate(vehicle);
        return vehicleRepo.save(vehicle);
    }

    @Override
    public void removeVehicle(String vehicleId) {
        if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
            throw new IllegalStateException("Cannot remove a rented vehicle.");
        }
        vehicleRepo.deleteById(vehicleId);
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}