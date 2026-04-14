package com.umcsuser.current.repositories;

import com.umcsuser.current.models.VehicleCategoryConfig;

import java.util.List;
import java.util.Optional;

public interface VehicleCategoryConfigRepository {
    List<VehicleCategoryConfig> findAll();
    Optional<VehicleCategoryConfig> findByCategory(String category);
}
