import com.umcsuser.current.models.Role;
import com.umcsuser.current.models.User;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.RentalRepository;
import com.umcsuser.current.repositories.UserRepository;
import com.umcsuser.current.repositories.VehicleRepository;
import com.umcsuser.current.repositories.impl.RentalJsonRepository;
import com.umcsuser.current.repositories.impl.UserJsonRepository;
import com.umcsuser.current.repositories.impl.VehicleJsonRepository;
import com.umcsuser.current.services.AuthService;
import com.umcsuser.current.services.RentalService;
import com.umcsuser.current.services.VehicleService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepo = new UserJsonRepository("users.json");
        VehicleRepository vehicleRepo = new VehicleJsonRepository("vehicles.json");
        RentalRepository rentalRepo = new RentalJsonRepository("rentals.json");

        AuthService authService = new AuthService(userRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo);
        RentalService rentalService = new RentalService(rentalRepo, vehicleRepo);

        Scanner scanner = new Scanner(System.in);
        User loggedUser = null;

        System.out.println("What do you wish to do?:\n1. Log in\n2. Register");
        int option = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Login:");
        String login = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();

        if (option == 1) {
            loggedUser = authService.login(login, password);
            if (loggedUser == null) {
                System.out.println("Invalid login or password.");
                return;
            }
            System.out.println("Logged in successfully.");
        } else if (option == 2) {
            loggedUser = authService.register(login, password);
            if (loggedUser == null) {
                System.out.println("Could not register (login taken).");
                return;
            }
            System.out.println("Registered and logged in successfully.");
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (loggedUser.getRole() == Role.USER) {
            while (option != 5) {
                System.out.println("\nWhat do you wish to do?\n1. View AVAILABLE vehicles\n2. Rent a vehicle\n3. Return a vehicle\n4. Show my rented vehicles\n5. Exit");
                option = scanner.nextInt();
                scanner.nextLine();

                if (option == 1) {
                    List<Vehicle> available = vehicleService.getAvailableVehicles();
                    System.out.println("Available vehicles:");
                    available.forEach(System.out::println);
                } else if (option == 2) {
                    System.out.println("Insert vehicle ID to rent:");
                    String id = scanner.nextLine();
                    if (rentalService.rentVehicle(loggedUser.getID(), id)) {
                        System.out.println("Vehicle rented successfully.");
                    } else {
                        System.out.println("Incorrect vehicle ID or vehicle already rented.");
                    }
                } else if (option == 3) {
                    System.out.println("Insert vehicle ID to return:");
                    String id = scanner.nextLine();
                    if (rentalService.returnVehicle(loggedUser.getID(), id)) {
                        System.out.println("Vehicle returned successfully.");
                    } else {
                        System.out.println("You don't have this vehicle rented.");
                    }
                } else if (option == 4) {
                    List<Vehicle> myRentals = rentalService.getUserActiveRentals(loggedUser.getID());
                    if (myRentals.isEmpty()) {
                        System.out.println("No vehicles rented.");
                    } else {
                        System.out.println("Your rented vehicles:");
                        myRentals.forEach(System.out::println);
                    }
                }
            }
        } else if (loggedUser.getRole() == Role.ADMIN) {
            while (option != 6) {
                System.out.println("\nWhat do you wish to do? (ADMIN)\n1. View ALL vehicles\n2. View user list\n3. Add a vehicle\n4. Remove a vehicle\n5. Remove a user\n6. Exit");
                option = scanner.nextInt();
                scanner.nextLine();

                if (option == 1) {
                    List<Vehicle> all = vehicleService.getAllVehicles();
                    System.out.println("All vehicles (incl. rented):");
                    all.forEach(System.out::println);
                } else if (option == 2) {
                    List<User> users = userRepo.findAll();
                    System.out.println("System users:");
                    users.forEach(System.out::println);
                } else if (option == 3) {
                    System.out.println("Insert vehicle ID:");
                    String id = scanner.nextLine();
                    System.out.println("Insert brand:");
                    String brand = scanner.nextLine();
                    System.out.println("Insert model:");
                    String model = scanner.nextLine();
                    System.out.println("Insert year:");
                    int year = scanner.nextInt();
                    System.out.println("Insert price:");
                    double price = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle newVehicle = Vehicle.builder()
                            .id(id).brand(brand).model(model)
                            .year(year).price(price).category("C")
                            .build();

                    if (vehicleService.addVehicle(newVehicle)) {
                        System.out.println("Vehicle added successfully.");
                    } else {
                        System.out.println("Vehicle with this ID already exists.");
                    }
                } else if (option == 4) {
                    System.out.println("Insert vehicle ID to remove:");
                    String id = scanner.nextLine();
                    if (vehicleService.removeVehicle(id)) {
                        System.out.println("Vehicle removed successfully.");
                    } else {
                        System.out.println("Cannot remove: Vehicle is currently rented or does not exist.");
                    }
                } else if (option == 5) {
                    System.out.println("Insert user ID to remove:");
                    String id = scanner.nextLine();
                    userRepo.deleteById(id);
                    System.out.println("User removed successfully.");
                }
            }
        }
    }
}