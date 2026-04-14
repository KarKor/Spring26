package com.umcsuser.current.models;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class Vehicle {
    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private double price;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, Object> atributes = new HashMap<>();

    @Builder
    public Vehicle(String id, String category, String brand, String model, int year, String plate, double price, Map<String, Object> atributes) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.atributes = atributes == null ? new HashMap<>() : new HashMap<>(atributes);
    }
}