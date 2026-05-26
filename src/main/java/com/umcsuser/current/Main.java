package com.umcsuser.current;

import com.umcsuser.current.repositories.*;
import com.umcsuser.current.repositories.impl.*;
import com.umcsuser.current.services.*;
import com.umcsuser.current.services.VehicleValidator;

public class Main {
    public static void main(String[] args) {
        String storageType = "json";

        for (String arg : args) {
            if ("--storage-jdbc".equals(arg)) {
                storageType = "jdbc";
                break;
            } else if ("--storage-hibernate".equals(arg)) {
                storageType = "hibernate";
                break;
            }
        }

        AuthServiceInterface authService;
        VehicleServiceInterface vehicleService;
        RentalServiceInterface rentalService;
        UserServiceInterface userService;

        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository("categories.json");
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator vehicleValidator = new VehicleValidator(configService);

        if ("hibernate".equals(storageType)) {
            System.out.println("App initialized using Hibernate");

            UserHibernateRepository userRepo = new UserHibernateRepository();
            VehicleHibernateRepository vehicleRepo = new VehicleHibernateRepository();
            RentalHibernateRepository rentalRepo = new RentalHibernateRepository();

            authService = new AuthHibernateService(userRepo);
            vehicleService = new VehicleHibernateService(vehicleRepo, rentalRepo);
            rentalService = new RentalHibernateService(rentalRepo, vehicleRepo, userRepo);
            userService = new UserHibernateService(userRepo, rentalRepo);

        } else if ("jdbc".equals(storageType)) {
            System.out.println("App initialized using JDBC");
            UserRepository userRepo = new UserJdbcRepository();
            VehicleRepository vehicleRepo = new VehicleJdbcRepository();
            RentalRepository rentalRepo = new RentalJdbcRepository();

            authService = new AuthService(userRepo);
            vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
            rentalService = new RentalService(rentalRepo, vehicleRepo, userRepo);
            userService = new UserService(userRepo, rentalRepo);

        } else {
            System.out.println("App initialized using JSON");
            UserRepository userRepo = new UserJsonRepository("users.json");
            VehicleRepository vehicleRepo = new VehicleJsonRepository("vehicles.json");
            RentalRepository rentalRepo = new RentalJsonRepository("rentals.json");

            authService = new AuthService(userRepo);
            vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
            rentalService = new RentalService(rentalRepo, vehicleRepo, userRepo);
            userService = new UserService(userRepo, rentalRepo);
        }

        UI ui = new UI(authService, vehicleService, rentalService, userService, configService);
        ui.start();
    }
}