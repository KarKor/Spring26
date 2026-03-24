public abstract class Vehicle {
    protected String brand;
    protected String model;
    protected int year;
    protected double price;
    protected boolean rented;
    protected String insideID;

    public Vehicle(String insideID, String brand, String model, int year, double price, boolean rented) {
        this.brand = brand;
        this.insideID = insideID;
        this.model = model;
        this.price = price;
        this.rented = rented;
        this.year = year;
    }

    public abstract Vehicle copy();

    public String getBrand() {
        return brand;
    }

    public String getInsideID() {
        return insideID;
    }

    public String getModel() {
        return model;
    }

    public double getPrice() {
        return price;
    }

    public boolean isRented() {
        return rented;
    }

    public int getYear() {
        return year;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public void rentOut(boolean rented) {
        this.rented = rented;
    }

    public String toCSV(){
        return brand+";"+model+";"+year+";"+price+";"+rented+";"+insideID;
    }

    @Override
    public String toString(){
        return "brand: " + brand
                + " model: " + model
                + " year: " + year
                + " price: " + price
                + " rented?: " + rented
                + " ID: " + insideID;
    }

}
