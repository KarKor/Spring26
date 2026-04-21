package com.umcsuser.current;

import com.umcsuser.current.models.Role;
import com.umcsuser.current.models.User;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.services.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UI {
    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final UserService userService;
    private final VehicleCategoryConfigService configService;

    private final Scanner scanner;
    private User loggedUser;

    public UI(AuthService authService, VehicleService vehicleService, RentalService rentalService,
              UserService userService, VehicleCategoryConfigService configService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.userService = userService;
        this.configService = configService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        handleLogin();
        if (loggedUser != null) {
            System.out.println("Welcome, " + loggedUser.getLogin() + " (" + loggedUser.getRole() + ")");
            handleMainMenu();
        }
    }

    private void handleLogin() {
        while (loggedUser == null) {
            System.out.println("1. Login\n2. Register\n3. Exit");
            String choice = scanner.nextLine();
            if (choice.equals("3")) return;

            if (choice.equals("1")) {
                System.out.println("Login:");
                String login = scanner.nextLine();
                System.out.println("Password:");
                String password = scanner.nextLine();
                loggedUser = authService.login(login, password);
                if (loggedUser == null) System.out.println("Invalid credentials.");
            } else if (choice.equals("2")) {
                System.out.println("Login:");
                String login = scanner.nextLine();
                System.out.println("Password:");
                String password = scanner.nextLine();
                loggedUser = authService.register(login, password);
                if (loggedUser == null) System.out.println("Registration failed.");
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private void handleMainMenu() {
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. List all vehicles");
            System.out.println("2. List available vehicles");
            System.out.println("3. Rent a vehicle");
            System.out.println("4. Show my rented vehicle");
            System.out.println("5. Return a vehicle");
            if (loggedUser.getRole() == Role.ADMIN) {
                System.out.println("6. List all users");
                System.out.println("7. View rental history");
                System.out.println("8. Add vehicle");
                System.out.println("9. Remove vehicle");
                System.out.println("10. Remove user");
            }
            System.out.println("0. Exit");

            String input = scanner.nextLine();
            int option;
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (option == 0) break;

            switch (option) {
                case 1 -> vehicleService.getAllVehicles().forEach(System.out::println);
                case 2 -> vehicleService.getAvailableVehicles().forEach(System.out::println);
                case 3 -> rentVehicleFlow();
                case 4 -> viewRentedVehicle();
                case 5 -> returnVehicleFlow();
                case 6 -> userService.getAllUsers().forEach(System.out::println);
                case 7 -> rentalService.getAllRentals().forEach(System.out::println);
                case 8 -> { if (isAdmin()) addVehicleFlow(); }
                case 9 -> { if (isAdmin()) removeVehicleFlow(); }
                case 10 -> { if (isAdmin()) removeUserFlow(); }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private boolean isAdmin() {
        if (loggedUser.getRole() != Role.ADMIN) {
            System.out.println("Insufficient permissions.");
            return false;
        }
        return true;
    }

    private void rentVehicleFlow() {
        System.out.println("Insert vehicle ID to rent:");
        String vehicleId = scanner.nextLine();
        if (rentalService.rentVehicle(loggedUser.getID(), vehicleId)) {
            System.out.println("Vehicle rented successfully.");
        } else {
            System.out.println("Error: Vehicle not found or already rented.");
        }
    }

    private void viewRentedVehicle(){
        List<Vehicle> activeRentals = rentalService.getUserActiveRentals(loggedUser.getID());
        if (activeRentals.isEmpty()) {
            System.out.println("No active rentals.");
            return;
        }

        activeRentals.forEach(System.out::println);
    }

    private void returnVehicleFlow() {
        List<Vehicle> activeRentals = rentalService.getUserActiveRentals(loggedUser.getID());
        if (activeRentals.isEmpty()) {
            System.out.println("No active rentals.");
            return;
        }

        activeRentals.forEach(System.out::println);
        System.out.println("Insert vehicle ID:");
        String vehicleId = scanner.nextLine();

        if (rentalService.returnVehicle(loggedUser.getID(), vehicleId)) {
            System.out.println("Vehicle returned successfully.");
        } else {
            System.out.println("Error: Invalid vehicle ID.");
        }
    }

    private void addVehicleFlow() {
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

            System.out.println("Insert Category (Car, Motorcycle, Bus):");
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
    }

    private void removeVehicleFlow() {
        System.out.println("Insert vehicle ID:");
        String id = scanner.nextLine();
        try {
            vehicleService.removeVehicle(id);
            System.out.println("Vehicle removed successfully.");
        } catch (IllegalStateException e) {
            System.out.println("Cannot remove vehicle: " + e.getMessage());
        }
    }

    private void removeUserFlow() {
        System.out.println("Insert user ID:");
        String id = scanner.nextLine();
        try {
            userService.removeUser(id);
            System.out.println("User removed successfully.");
        } catch (IllegalStateException e) {
            System.out.println("Cannot remove user: " + e.getMessage());
        }
    }
}