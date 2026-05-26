package com.umcsuser.current.services;


import com.umcsuser.current.models.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalServiceInterface {

    Rental rentVehicle(String userId, String vehicleId);

    Rental returnVehicle(String userId);

    Optional<Rental> findActiveRentalByUserId(String userId);

    List<Rental> findAllRentals();

    List<Rental> findUserRentals(String userId);

    boolean userHasActiveRental(String userId);

    boolean vehicleHasActiveRental(String vehicleId);
}