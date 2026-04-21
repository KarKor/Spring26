package com.umcsuser.current.services;

import com.umcsuser.current.models.VehicleCategoryConfig;
import com.umcsuser.current.repositories.VehicleCategoryConfigRepository;

import java.util.Optional;

public class VehicleCategoryConfigService {
    private final VehicleCategoryConfigRepository repository;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository repository) {
        this.repository = repository;
    }

    public Optional<VehicleCategoryConfig> getConfigForCategory(String category) {
        return repository.findByCategory(category);
    }
}