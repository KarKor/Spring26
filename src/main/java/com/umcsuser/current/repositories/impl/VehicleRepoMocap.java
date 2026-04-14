package com.umcsuser.current.repositories.impl;

import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleRepoMocap implements VehicleRepository {

    private final List<Vehicle> vehicles = new ArrayList<>();

    @Override
    public List<Vehicle> findAll() {
        return vehicles;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        vehicles.removeIf(v -> v.getId() != null && v.getId().equals(vehicle.getId()));
        vehicles.add(vehicle);
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        vehicles.removeIf(v -> v.getId().equals(id));
    }
}