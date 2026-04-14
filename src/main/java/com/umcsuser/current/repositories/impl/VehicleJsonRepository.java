package com.umcsuser.current.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.current.db.JsonFileStorage;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.VehicleRepository;

import java.util.List;
import java.util.Optional;

public class VehicleJsonRepository implements VehicleRepository {
    private final JsonFileStorage<Vehicle> storage;

    public VehicleJsonRepository(String filename) {
        this.storage = new JsonFileStorage<>(filename, new TypeToken<List<Vehicle>>(){}.getType());
    }

    @Override
    public List<Vehicle> findAll() { return storage.load(); }

    @Override
    public Optional<Vehicle> findById(String id) {
        return findAll().stream().filter(v -> v.getId().equals(id)).findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        List<Vehicle> vehicles = findAll();
        vehicles.removeIf(v -> v.getId().equals(vehicle.getId()));
        vehicles.add(vehicle);
        storage.save(vehicles);
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        List<Vehicle> vehicles = findAll();
        vehicles.removeIf(v -> v.getId().equals(id));
        storage.save(vehicles);
    }
}