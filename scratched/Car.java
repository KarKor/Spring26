package com.umcsuser.current.models;

public class Car extends Vehicle {
    public Car(String insideID, String brand, String model, int year, double price, boolean rented) {
        super(insideID, brand, model, year, price, rented);
    }

    @Override
    public Car copy(){
        return new Car(insideID, brand, model, year, price, rented);
    }

    @Override
    public String toCSV(){
        return "CAR;"+insideID+";"+brand+";"+model+";"+year+";"+price+";"+rented;
    }
}
