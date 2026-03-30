import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        VehicleRepositoryImpl repo = new VehicleRepositoryImpl();
        UserRepository urepo = new UserRepository();
        Authentication auth = new Authentication(urepo);
        User user;
        boolean loggedIn=false;

        Scanner scanner = new Scanner(System.in);
        int option = 0;

        System.out.println("What do you wish to do?:\n1. Log in\n2. Register ");
        option=scanner.nextInt();
        scanner.nextLine();
        if(option==1) {
            System.out.println("Login:");
            String login = scanner.nextLine();
            System.out.println("Password:");
            String password = scanner.nextLine();
            user = auth.authenticate(login, password);
            if (user == null) {
                System.out.println("invalid login or password");
                return;
            }
            loggedIn=true;
        }
        else{
            System.out.println("Login:");
            String login = scanner.nextLine();
            System.out.println("Password:");
            String password = scanner.nextLine();
            user = auth.register(login, password);
            if(user==null) System.out.println("invalid credentials");
            else {
                System.out.println("Registered and logged in successfully");
                loggedIn=true;
            }
        }

        if (loggedIn){
            if (user.getRole() == Role.USER) {
                while (option != 5) {
                    System.out.println("What do you wish to do? \n 1. View vehicle list \n 2. Rent a vehicle \n 3. Return a vehicle \n 4. Show rented vehicle \n 5. Exit");

                    option = scanner.nextInt();
                    scanner.nextLine();
                    if (option == 1) {
                        List<Vehicle> print = repo.getVehicles();
                        for (Vehicle vehicle : print) System.out.println(vehicle.toString());
                    }
                    if (option == 2) {
                        System.out.println("Insert vehicle ID");
                        int ID = scanner.nextInt();
                        if (!repo.isRented(ID)) {
                            repo.rentVehicle(String.valueOf(ID));
                            user.setRentedVehicleID(String.valueOf(ID));
                            System.out.println("Vehicle rented successfully");
                            repo.save();
                        } else System.out.println("Incorrect vehicle ID or vehicle already rented out");
                    }
                    if (option == 3) {
                        //System.out.println("Insert vehicle ID");
                        //int ID = scanner.nextInt();
                        //if (repo.isRented(ID)) {
                            repo.returnVehicle(String.valueOf(user.getRentedVehicleID()));
                            user.setRentedVehicleID(null);
                            System.out.println("Vehicle returned successfully");
                            repo.save();
                        //} else System.out.println("Incorrect vehicle ID or vehicle not rented out");
                    }
                    if (option == 4) {
                        Vehicle rented = repo.getVehicle(user.getRentedVehicleID());
                        if (rented != null) System.out.println(rented.toString() + '\n');
                        else System.out.println("No vehicle rented");
                    }
                }
            } else {
                while (option != 6) {
                    System.out.println("What do you wish to do? \n 1. View vehicle list \n 2. View user list \n 3. Add a vehicle \n 4. Remove a vehicle \n 5. Remove a user\n 6. Exit");
                    option = scanner.nextInt();
                    scanner.nextLine();
                    if (option == 1) {
                        List<Vehicle> print = repo.getVehicles();
                        for (Vehicle vehicle : print) System.out.println(vehicle.toString());
                    }
                    if (option == 2) {
                        List<User> print = urepo.getUsers();
                        for (User usr : print) {
                            /*if(usr.rented())*/
                            System.out.println(usr.toString() + ' ' + repo.getVehicle(usr.getRentedVehicleID()));
                            //else System.out.println(usr.toString());
                        }
                    }
                    if (option == 3) {
                        System.out.println("Insert vehicle ID");
                        int ID;
                        while (true) {
                            ID = scanner.nextInt();
                            if (repo.validID(String.valueOf(ID)))
                                System.out.println("vehicle with this ID already exists");
                            else break;
                        }
                        scanner.nextLine();
                        System.out.println("Insert brand:");
                        String brand = scanner.nextLine();
                        System.out.println("Insert model:");
                        String model = scanner.nextLine();
                        System.out.println("Insert year:");
                        int year = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Insert price:");
                        double price = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.println("Motorcycle (M) or car (C)?");
                        String cat = scanner.nextLine();
                        if(cat.equals("M")){
                            System.out.println("Insert motorcycle category");
                            MotorcycleCategory category = MotorcycleCategory.valueOf(scanner.nextLine());
                            repo.add(new Motorcycle(String.valueOf(ID), brand, model, year, price, false, category));
                        }
                        if(cat.equals("C")){
                            repo.add(new Car(String.valueOf(ID), brand, model, year, price, false));
                        }
                        System.out.println("Vehicle added successfully");
                    }
                    if (option == 4) {
                        System.out.println("Insert vehicle ID");
                        int ID = scanner.nextInt();
                        if (repo.validID(String.valueOf(ID))) {
                            if(repo.remove(String.valueOf(ID))) {
                                user.setRentedVehicleID(null);
                                System.out.println("Vehicle removed successfully");
                                repo.save();
                            } else {
                                System.out.println("vehicle rented out - could not remove");
                            }
                        } else System.out.println("Incorrect vehicle ID");
                    }
                    if (option == 5) {
                        System.out.println("Insert user login");
                        String login = scanner.nextLine();
                        if (urepo.remove(login)) System.out.println("User removed successfully");
                        else System.out.println("Could not remove user");
                    }
                }
            }
        }
    }
}
