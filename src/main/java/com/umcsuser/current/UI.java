package com.umcsuser.current;

import com.umcsuser.current.models.Rental;
import com.umcsuser.current.models.Role;
import com.umcsuser.current.models.User;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.models.VehicleCategoryConfig;
import com.umcsuser.current.services.*;

import java.util.*;

public class UI {
    private final AuthServiceInterface authService;
    private final VehicleServiceInterface vehicleService;
    private final RentalServiceInterface rentalService;
    private final UserServiceInterface userService;
    private final VehicleCategoryConfigService configService;

    private final Scanner scanner;
    private User loggedUser;
    boolean exit = false;

    public UI(AuthServiceInterface authService,
              VehicleServiceInterface vehicleService,
              RentalServiceInterface rentalService,
              UserServiceInterface userService,
              VehicleCategoryConfigService configService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.userService = userService;
        this.configService = configService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while(!exit){
            handleLogin();
            if (loggedUser != null) {
                System.out.println("Welcome, " + loggedUser.getLogin() + " (" + loggedUser.getRole() + ")");
                handleMainMenu();
            }
        }
    }

    private void handleLogin() {
        while (loggedUser == null) {
            System.out.println("1. Login\n2. Register\n3. Exit");
            String choice = scanner.nextLine();
            if (choice.equals("3")) {
                exit=true;
                return;
            }

            if (choice.equals("1")) {
                System.out.println("Login:");
                String login = scanner.nextLine();
                System.out.println("Password:");
                String password = scanner.nextLine();

                Optional<User> userOpt = authService.login(login, password);
                if (userOpt.isPresent()) {
                    loggedUser = userOpt.get();
                } else {
                    System.out.println("Invalid credentials.");
                }

            } else if (choice.equals("2")) {
                System.out.println("Login:");
                String login = scanner.nextLine();
                System.out.println("Password:");
                String password = scanner.nextLine();

                boolean success = authService.register(login, password);
                if (success) {
                    System.out.println("Registration successful. You can now log in.");
                } else {
                    System.out.println("Registration failed.");
                }
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
            System.out.println("0. Log out");

            String input = scanner.nextLine();
            int option;
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (option == 0){
                loggedUser=null;
                break;
            }

            switch (option) {
                case 1 -> vehicleService.findAllVehicles().forEach(System.out::println);
                case 2 -> vehicleService.findAvailableVehicles().forEach(System.out::println);
                case 3 -> rentVehicleFlow();
                case 4 -> viewRentedVehicle();
                case 5 -> returnVehicleFlow();
                case 6 -> userService.findAllUsers().forEach(System.out::println);
                case 7 -> rentalService.findAllRentals().forEach(System.out::println);
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
        try {
            rentalService.rentVehicle(loggedUser.getId(), vehicleId);
            System.out.println("Vehicle rented successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewRentedVehicle(){
        Optional<Rental> activeRental = rentalService.findActiveRentalByUserId(loggedUser.getId());
        if (activeRental.isEmpty()) {
            System.out.println("No active rentals.");
            return;
        }

        System.out.println(activeRental.get().getVehicle());
    }

    private void returnVehicleFlow() {
        try {
            rentalService.returnVehicle(loggedUser.getId());
            System.out.println("Vehicle returned successfully.");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addVehicleFlow() {
        try {
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
            VehicleCategoryConfig config = configService.getByCategory(category);
            if (config.getAttributes() != null) {
                System.out.println("Adding attributes for category: " + category);
                for (Map.Entry<String, String> entry : config.getAttributes().entrySet()) {
                    System.out.println("Enter value for " + entry.getKey() + ":");
                    String val = scanner.nextLine();
                    switch (entry.getValue().toLowerCase()) {
                        case "number" -> attributes.put(entry.getKey(), Integer.parseInt(val));
                        default -> attributes.put(entry.getKey(), val);
                    }
                }
            }

            Vehicle newVehicle = Vehicle.builder()
                    .id(UUID.randomUUID().toString())
                    .brand(brand)
                    .model(model)
                    .year(year)
                    .plate(plate)
                    .price(price)
                    .category(category)
                    .attributes(attributes)
                    .build();

            Vehicle savedVehicle = vehicleService.addVehicle(newVehicle);
            if (savedVehicle != null) {
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
            userService.deleteUser(id, loggedUser.getId());
            System.out.println("User removed successfully.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Cannot remove user: " + e.getMessage());
        }
    }
}