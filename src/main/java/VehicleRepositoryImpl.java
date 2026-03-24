import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VehicleRepositoryImpl implements IVehicleRepository{
    private final List<Vehicle> vehicles;

    public boolean validID(String id){
        for(Vehicle vehicle: vehicles) if(vehicle.getInsideID().equals(id)) return true;
        return false;
    }

    public VehicleRepositoryImpl() {
        this.vehicles = new ArrayList<>();
        load();
    }

    @Override
    public boolean add(Vehicle vehicle) {
        if (getVehicle(vehicle.getInsideID()) != null) {
            return false;
        }
        vehicles.add(vehicle);
        save();
        return true;
    }

    public boolean remove(String id){
        for(Vehicle vehicle: vehicles){
            if(Integer.parseInt(vehicle.getInsideID())==Integer.parseInt(id)) {
                vehicles.remove(vehicle);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean rentVehicle(String id) {
        for(Vehicle vehicle : vehicles){
            if(Integer.parseInt(vehicle.getInsideID())==Integer.parseInt(id) && !vehicle.isRented()) {
                vehicle.rentOut(true);
                save();
                return true;
            }
        }
        return false;
        //vehicles.get(id).rentOut(true);
    }

    @Override
    public boolean returnVehicle(String id) {
        for(Vehicle vehicle : vehicles){
            if(Integer.parseInt(vehicle.getInsideID())==Integer.parseInt(id) && vehicle.isRented()) {
                vehicle.rentOut(false);
                save();
                return true;
            }
        }
        return false;
    }

    public boolean isRented(int id){
        for (Vehicle vehicle : vehicles){
            if(Integer.parseInt(vehicle.getInsideID())==id) return vehicle.isRented();
        }
        return false;
    }

    @Override
    public List<Vehicle> getVehicles() {
        ArrayList<Vehicle> copied = new ArrayList<>();
        for(Vehicle vehicle : vehicles) copied.add(vehicle.copy());

        return copied;
    }

    @Override
    public Vehicle getVehicle(String id){
        for(Vehicle vehicle : vehicles){
            if(vehicle.getInsideID().equals(id)) {
                return vehicle;
            }
        }
        return null;
    }

    @Override
    public void save() {
        try{
            PrintWriter writer = new PrintWriter("vehicles.csv");
            for(Vehicle vehicle : vehicles) {
                writer.println(vehicle.toCSV());
            }
            writer.close();
            System.out.println("saved");
        } catch (FileNotFoundException e){
            System.out.println("file not found");
        }

    }

    @Override
    public void load() {
        vehicles.clear();

        try{
            File file = new File("vehicles.csv");
            Scanner scannerF = new Scanner(file);

            while (scannerF.hasNextLine()){
                String line = scannerF.nextLine();
                String[] parts = line.split(";");
                int t1=Integer.parseInt(parts[4]);
                double t2=Double.parseDouble(parts[5]);
                boolean t3=Boolean.parseBoolean(parts[6]);
                if(parts[0].equals("CAR")){
                    vehicles.add(new Car(parts[1], parts[2], parts[3], t1, t2, t3));
                }else{
                    MotorcycleCategory cat = MotorcycleCategory.valueOf(parts[7]);
                    vehicles.add(new Motorcycle(parts[1], parts[2], parts[3], t1, t2, t3, cat));
                }
            }
            scannerF.close();
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        }
    }
}
