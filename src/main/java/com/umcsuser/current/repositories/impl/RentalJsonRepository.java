package com.umcsuser.current.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.current.db.JsonFileStorage;
import com.umcsuser.current.models.Rental;
import com.umcsuser.current.repositories.RentalRepository;

import java.util.List;
import java.util.Optional;

public class RentalJsonRepository implements RentalRepository {
    private final JsonFileStorage<Rental> storage;

    public RentalJsonRepository(String filename) {
        this.storage = new JsonFileStorage<>(filename, new TypeToken<List<Rental>>(){}.getType());
    }

    @Override
    public List<Rental> findAll() { return storage.load(); }

    @Override
    public Optional<Rental> findById(String id) {
        return findAll().stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public Rental save(Rental rental) {
        List<Rental> rentals = findAll();
        rentals.removeIf(r -> r.getId().equals(rental.getId()));
        rentals.add(rental);
        storage.save(rentals);
        return rental;
    }

    @Override
    public void deleteById(String id) {
        List<Rental> rentals = findAll();
        rentals.removeIf(r -> r.getId().equals(id));
        storage.save(rentals);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return findAll().stream()
                .filter(r -> r.getVehicleId().equals(vehicleId) && r.isActive())
                .findFirst();
    }
}