package com.umcsuser.current;

import com.umcsuser.current.repositories.*;
import com.umcsuser.current.repositories.impl.*;
import com.umcsuser.current.services.*;
import com.umcsuser.current.services.VehicleValidator;

public class Main {
    public static void main(String[] args) {
        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository("categories.json");
        UserRepository userRepo = new UserJsonRepository("users.json");
        VehicleRepository vehicleRepo = new VehicleJsonRepository("vehicles.json");
        RentalRepository rentalRepo = new RentalJsonRepository("rentals.json");

        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator vehicleValidator = new VehicleValidator(configService);

        AuthService authService = new AuthService(userRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
        RentalService rentalService = new RentalService(rentalRepo, vehicleRepo);
        UserService userService = new UserService(userRepo, rentalRepo);

        UI ui = new UI(authService, vehicleService, rentalService, userService, configService);
        ui.start();
    }
}