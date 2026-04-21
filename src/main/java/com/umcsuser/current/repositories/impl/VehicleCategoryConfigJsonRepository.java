package com.umcsuser.current.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.current.db.JsonFileStorage;
import com.umcsuser.current.models.VehicleCategoryConfig;
import com.umcsuser.current.repositories.VehicleCategoryConfigRepository;

import java.util.List;
import java.util.Optional;

public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {
    private final JsonFileStorage<VehicleCategoryConfig> storage;

    public VehicleCategoryConfigJsonRepository(String filename) {
        this.storage = new JsonFileStorage<>(filename, new TypeToken<List<VehicleCategoryConfig>>(){}.getType());
    }

    @Override
    public List<VehicleCategoryConfig> findAll() {
        return storage.load();
    }

    @Override
    public Optional<VehicleCategoryConfig> findByCategory(String category) {
        return findAll().stream()
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .findFirst();
    }
}