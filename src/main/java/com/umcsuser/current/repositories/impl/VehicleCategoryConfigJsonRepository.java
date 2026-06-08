package com.umcsuser.current.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.current.db.JsonFileStorage;
import com.umcsuser.current.models.VehicleCategoryConfig;
import com.umcsuser.current.repositories.VehicleCategoryConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {

    private final JsonFileStorage<VehicleCategoryConfig> storage;

    public VehicleCategoryConfigJsonRepository(@Value("${data.categories.path:categories.json}") String filename) {
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