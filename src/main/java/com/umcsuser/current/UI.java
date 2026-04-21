package com.umcsuser.current;

import com.umcsuser.current.models.Role;
import com.umcsuser.current.models.User;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.models.VehicleCategoryConfig;
import com.umcsuser.current.services.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UI {
    private final VehicleCategoryConfigService configService;
    private final VehicleService vehicleService;
    private final AuthService authService;
    private final RentalService rentalService;
    private final UserService userService;

    public UI(VehicleCategoryConfigService configService, VehicleService vehicleService,
              AuthService authService, RentalService rentalService, UserService userService) {
        this.configService = configService;
        this.vehicleService = vehicleService;
        this.authService = authService;
        this.rentalService = rentalService;
        this.userService = userService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        User loggedUser = null;

        System.out.println("1. Log in\n2. Register");
        int option = Integer.parseInt(scanner.nextLine());
        System.out.println("Login:"); String login = scanner.nextLine();
        System.out.println("Password:"); String password = scanner.nextLine();

        if (option == 1) {
            loggedUser = authService.login(login, password);
            if (loggedUser == null) { System.out.println("Błędne dane logowania."); return; }
        } else {
            loggedUser = authService.register(login, password);
            if (loggedUser == null) return;
        }

        while (true) {
            if (loggedUser.getRole() == Role.USER) {
                System.out.println("\n1. Pokaż dostępne\n2. Wypożycz\n3. Zwróć\n4. Moje wypożyczenia\n5. Wyjdź");
                option = Integer.parseInt(scanner.nextLine());

                if (option == 1) vehicleService.getAvailableVehicles().forEach(System.out::println);
                else if (option == 2) {
                    System.out.println("ID pojazdu:");
                    if (rentalService.rentVehicle(loggedUser.getID(), scanner.nextLine())) System.out.println("Wypożyczono.");
                    else System.out.println("Błąd wypożyczenia.");
                } else if (option == 3) {
                    System.out.println("ID pojazdu:");
                    if (rentalService.returnVehicle(loggedUser.getID(), scanner.nextLine())) System.out.println("Zwrócono.");
                    else System.out.println("Błąd zwrotu.");
                } else if (option == 4) {
                    rentalService.getUserActiveRentals(loggedUser.getID()).forEach(System.out::println);
                } else if (option == 5) break;

            } else { // ADMIN
                System.out.println("\n1. Pokaż wszystkie\n2. Użytkownicy\n3. Dodaj pojazd\n4. Usuń pojazd\n5. Usuń usera\n6. Wyjdź");
                option = Integer.parseInt(scanner.nextLine());

                if (option == 1) vehicleService.findAllVehicles().forEach(System.out::println);
                else if (option == 2) userService.getAllUsers().forEach(System.out::println);
                else if (option == 3) {
                    try {
                        System.out.println("ID:"); String id = scanner.nextLine();
                        System.out.println("Kategoria (dostępne: Car, Motorcycle, Bus):"); String cat = scanner.nextLine();
                        VehicleCategoryConfig config = configService.getByCategory(cat);

                        System.out.println("Marka:"); String brand = scanner.nextLine();
                        System.out.println("Model:"); String model = scanner.nextLine();
                        System.out.println("Rok:"); int year = Integer.parseInt(scanner.nextLine());
                        System.out.println("Rejestracja:"); String plate = scanner.nextLine();
                        System.out.println("Cena:"); double price = Double.parseDouble(scanner.nextLine());

                        Map<String, Object> attributes = new HashMap<>();
                        config.getAttributes().forEach((attr, typ) -> {
                            System.out.println("Podaj atrybut '" + attr + "' (" + typ + "):");
                            attributes.put(attr, scanner.nextLine()); // Prostota: przyjmujemy jako string, validator weryfikuje parsowalność
                        });

                        Vehicle vehicle = Vehicle.builder()
                                .id(id).category(cat).brand(brand).model(model)
                                .year(year).plate(plate).price(price).attributes(attributes).build();

                        Vehicle added = vehicleService.addVehicle(vehicle);
                        System.out.println("\nPojazd dodany pomyślnie:\n" + added);
                    } catch (Exception e) {
                        System.out.println("Błąd: " + e.getMessage());
                    }
                } else if (option == 4) {
                    System.out.println("ID pojazdu:");
                    try {
                        vehicleService.removeVehicle(scanner.nextLine());
                        System.out.println("Pojazd usunięty.");
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                } else if (option == 5) {
                    System.out.println("ID użytkownika:");
                    try {
                        userService.removeUser(scanner.nextLine());
                        System.out.println("Użytkownik usunięty.");
                    } catch (Exception e) { System.out.println(e.getMessage()); }
                } else if (option == 6) break;
            }
        }
    }
}