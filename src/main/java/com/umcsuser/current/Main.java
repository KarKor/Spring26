package com.umcsuser.current;

import com.umcsuser.current.models.*;
import com.umcsuser.current.repositories.*;
import com.umcsuser.current.repositories.impl.*;
import com.umcsuser.current.services.*;
import com.umcsuser.current.services.VehicleValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository("categories.json");
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);

        UserRepository userRepo = new UserJsonRepository("users.json");
        VehicleRepository vehicleRepo = new VehicleJsonRepository("vehicles.json");
        RentalRepository rentalRepo = new RentalJsonRepository("rentals.json");

        VehicleValidator vehicleValidator = new VehicleValidator(configService);

        AuthService authService = new AuthService(userRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo, vehicleValidator);
        RentalService rentalService = new RentalService(rentalRepo, vehicleRepo);
        UserService userService = new UserService(userRepo, rentalRepo);

        Scanner scanner = new Scanner(System.in);
        User loggedUser = null;

        while (loggedUser == null) {
            System.out.println("1. Login\n2. Register\n3. Exit");
            String choice = scanner.nextLine();
            if (choice.equals("3")) return;

            System.out.println("Login:");
            String login = scanner.nextLine();
            System.out.println("Password:");
            String password = scanner.nextLine();

            if (choice.equals("1")) {
                loggedUser = authService.login(login, password);
                if (loggedUser == null) System.out.println("Invalid credentials.");
            } else {
                loggedUser = authService.register(login, password);
                if (loggedUser == null) System.out.println("Registration failed.");
            }
        }

        System.out.println("Welcome, " + loggedUser.getLogin() + " (" + loggedUser.getRole() + ")");

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. List all vehicles");
            System.out.println("2. List available vehicles");
            if (loggedUser.getRole() == Role.ADMIN) {
                System.out.println("3. Add vehicle (Config-Driven)");
                System.out.println("4. Remove vehicle");
                System.out.println("5. Remove user");
            }
            System.out.println("0. Exit");

            int option = Integer.parseInt(scanner.nextLine());

            if (option == 0) break;

            if (option == 1) {
                vehicleService.getAllVehicles().forEach(System.out::println);
            } else if (option == 2) {
                vehicleService.getAvailableVehicles().forEach(System.out::println);
            } else if (loggedUser.getRole() == Role.ADMIN) {
                if (option == 3) {
                    try {
                        System.out.println("Insert ID:");
                        String id = scanner.nextLine();
                        System.out.println("Insert Brand:");
                        String brand = scanner.nextLine();
                        System.out.println("Insert Model:");
                        String model = scanner.nextLine();
                        System.out.println("Insert Year:");
                        int year = Integer.parseInt(scanner.nextLine());
                        System.out.println("Insert Plate:");
                        String plate = scanner.nextLine();
                        System.out.println("Insert Price:");
                        double price = Double.parseDouble(scanner.nextLine());

                        System.out.println("Insert Category (e.g., Car, Motorcycle, Bus):");
                        String category = scanner.nextLine();

                        Map<String, Object> attributes = new HashMap<>();
                        configService.getConfigForCategory(category).ifPresent(config -> {
                            if (config.getAttributes() != null) {
                                System.out.println("Adding attributes for category: " + category);
                                for (String attrName : config.getAttributes().keySet()) {
                                    System.out.println("Enter value for " + attrName + ":");
                                    attributes.put(attrName, scanner.nextLine());
                                }
                            }
                        });

                        Vehicle newVehicle = Vehicle.builder()
                                .id(id).brand(brand).model(model)
                                .year(year).plate(plate).price(price)
                                .category(category).attributes(attributes)
                                .build();

                        if (vehicleService.addVehicle(newVehicle)) {
                            System.out.println("Vehicle added successfully.");
                        } else {
                            System.out.println("Error: Vehicle with this ID already exists.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error adding vehicle: " + e.getMessage());
                    }

                } else if (option == 4) {
                    System.out.println("Insert vehicle ID to remove:");
                    String id = scanner.nextLine();
                    try {
                        vehicleService.removeVehicle(id);
                        System.out.println("Vehicle removed successfully.");
                    } catch (IllegalStateException e) {
                        System.out.println("Cannot remove: " + e.getMessage());
                    }

                } else if (option == 5) {
                    System.out.println("Insert user ID to remove:");
                    String id = scanner.nextLine();
                    try {
                        userService.removeUser(id);
                        System.out.println("User removed successfully.");
                    } catch (IllegalStateException e) {
                        System.out.println("Cannot remove: " + e.getMessage());
                    }
                }
            }
        }
    }
}