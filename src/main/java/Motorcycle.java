public class Motorcycle extends Vehicle{
    private MotorcycleCategory category;

    public Motorcycle(String insideID, String brand, String model, int year, double price, boolean rented, MotorcycleCategory category) {
        super(insideID, brand, model, year, price, rented);
        this.category = category;
    }

    @Override
    public Motorcycle copy(){
        return new Motorcycle(insideID, brand, model, year, price, rented, category);
    }

    @Override
    public String toCSV(){
        return "MOTORCYCLE;"+insideID+";"+brand+";"+model+";"+year+";"+price+";"+rented+";"+category;
    }

    public void setLicense(MotorcycleCategory category) {
        this.category = category;
    }

    public MotorcycleCategory getLicense() {
        return category;
    }
}
