import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        VehicleRepositoryImpl repo = new VehicleRepositoryImpl();
        UserRepository urepo = new UserRepository();
        Authentication auth = new Authentication(urepo);

        int option = 0;
        System.out.println("Login:");
        Scanner scanner = new Scanner(System.in);
        String login = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();
        User user = auth.authenticate(login, password);
        if (user == null) return;

        if (user.getRole() == Role.USER) {
            while (option != 5) {
                System.out.println("What do you wish to do? \n 1. View vehicle list \n 2. Rent a vehicle \n 3. Return a vehicle \n 4. Show rented vehicle \n 5. Exit");

                option = scanner.nextInt();
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
                    System.out.println("Insert vehicle ID");
                    int ID = scanner.nextInt();
                    if (repo.isRented(ID)) {
                        repo.returnVehicle(String.valueOf(ID));
                        user.setRentedVehicleID(null);
                        System.out.println("Vehicle returned successfully");
                        repo.save();
                    } else System.out.println("Incorrect vehicle ID or vehicle not rented out");
                }
                if (option == 4) {
                    Vehicle rented = repo.getVehicle(user.getRentedVehicleID());
                    if (rented != null) System.out.println(rented.toString() + '\n');
                    else System.out.println("No vehicle rented");
                }
            }
        } else {
            while (option != 5) {
                System.out.println("What do you wish to do? \n 1. View vehicle list \n 2. View user list \n 3. Add a vehicle \n 4. Remove a vehicle \n 5. Exit");
                option = scanner.nextInt();
                if (option == 1) {
                    List<Vehicle> print = repo.getVehicles();
                    for (Vehicle vehicle : print) System.out.println(vehicle.toString());
                }
                if (option == 2) {
                    List<User> print = urepo.getUsers();
                    for (User usr: print) {
                        /*if(usr.rented())*/ System.out.println(usr.toString()+' '+repo.getVehicle(usr.getRentedVehicleID()));
                        //else System.out.println(usr.toString());
                    }
                }
                if (option == 3) {
                    System.out.println("Insert vehicle ID");
                    while (true) {
                        int ID = scanner.nextInt();
                        if (repo.validID(String.valueOf(ID))) System.out.println("vehicle with this ID already exists");
                        else break;
                    }
                    System.out.println("Insert brand:");
                    String brand=scanner.nextLine();
                    System.out.println("Insert model:");
                    String model=scanner.nextLine();
                    System.out.println("Insert year:");
                    int year = scanner.nextInt();
                }
                if (option == 4) {
                    System.out.println("Insert vehicle ID");
                    int ID = scanner.nextInt();
                    if (repo.validID(String.valueOf(ID))) {
                        repo.remove(String.valueOf(ID));
                        user.setRentedVehicleID(null);
                        System.out.println("Vehicle removed successfully");
                        repo.save();
                    } else System.out.println("Incorrect vehicle ID");
                }
            }
        }
    }
}
