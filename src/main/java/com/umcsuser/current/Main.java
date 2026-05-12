package com.umcsuser.current;

import com.umcsuser.current.repositories.*;
import com.umcsuser.current.repositories.impl.*;
import com.umcsuser.current.services.*;
import com.umcsuser.current.services.VehicleValidator;

public class Main {
    public static void main(String[] args) {
        boolean useJdbc = false;

        for (String arg : args) {
            if ("--storage-jdbc".equals(arg)) {
                useJdbc = true;
                break;
            }
        }

        UserRepository userRepo;
        VehicleRepository vehicleRepo;
        RentalRepository rentalRepo;

        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository("categories.json");

        if (useJdbc) {
            System.out.println("App initialized using JDBC");
            userRepo = new UserJdbcRepository();
            vehicleRepo = new VehicleJdbcRepository();
            rentalRepo = new RentalJdbcRepository();
        } else {
            System.out.println("App initialized using JSON");
            userRepo = new UserJsonRepository("users.json");
            vehicleRepo = new VehicleJsonRepository("vehicles.json");
            rentalRepo = new RentalJsonRepository("rentals.json");
        }

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