import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class UserRepository implements IUserRepository{
    private final List<User> users;

    public UserRepository(){
        this.users=new ArrayList<>();
        load();
    }

    @Override
    public User getUser(String login) {
        for(User user:users){
            if(Objects.equals(user.getLogin(), login)) return user.copy();
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        ArrayList<User> copied = new ArrayList<>();
        for(User user:users) copied.add(user.copy());

        return copied;
    }

    @Override
    public boolean update(User user) {
        for(int i = 0; i < users.size(); i++) {
            if(users.get(i).getLogin().equals(user.getLogin())) {
                users.set(i, user);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public void save() {
        try{
            PrintWriter writer = new PrintWriter("users.csv");
            for(User user: users){
                writer.println(user.toCSV());
            }
        }catch (FileNotFoundException e){
            System.out.println("File not found");
        }

    }

//    @Override
//    public void load() {
//        users.clear();
//        try {
//            File file = new File("users.csv");
//            Scanner scannerF = new Scanner(file);
//
//            while (scannerF.hasNextLine()){
//                String line = scannerF.nextLine();
//                String[] parts = line.split(";");
//                users.add(new User(parts[0], Hasher.hashPassword(parts[1]), Role.valueOf(parts[2])));
//            }
//            scannerF.close();
//        }catch (FileNotFoundException e){
//            System.out.println("File not found");
//        }
//    }

    @Override
    public void load() {
        users.clear();
        try {
            File file = new File("users.csv");
            Scanner scannerF = new Scanner(file);

            while (scannerF.hasNextLine()){
                String line = scannerF.nextLine();

                String[] parts = line.split(";", -1);

                String rentedVehicleId = null;
                if (parts.length > 3 && !parts[3].isEmpty() && !parts[3].equals("NONE")) {
                    rentedVehicleId = parts[3];
                }

                users.add(new User(parts[0], parts[1], Role.valueOf(parts[2]), rentedVehicleId));
            }
            scannerF.close();
        } catch (Exception e){
            System.out.println("User or file does not exist.");
        }
    }
}
