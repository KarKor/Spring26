package com.umcsuser.current.models;

import lombok.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@ToString
public class Vehicle {

    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private double price;

    public Vehicle build() {
        String newId = (id == null || id.isBlank()) ? java.util.UUID.randomUUID().toString() : id;
        Vehicle vehicle;
        if ("Car".equalsIgnoreCase(category)) {
            vehicle = Vehicle.builder()
                    .id(newId)
                    .category(category)
                    .brand(brand)
                    .model(model)
                    .year(year)
                    .plate(plate)
                    .price(price)
                    .attributes(new HashMap<>(attributes))
                    .build();
        } else if ("Motorcycle".equalsIgnoreCase(category)) {
            vehicle = Vehicle.builder()
                    .id(newId)
                    .category(category)
                    .brand(brand)
                    .model(model)
                    .year(year)
                    .plate(plate)
                    .price(price)
                    .attributes(new HashMap<>(attributes))
                    .build();
        } else {
            vehicle = Vehicle.builder()
                    .id(newId)
                    .category(category)
                    .brand(brand)
                    .model(model)
                    .year(year)
                    .plate(plate)
                    .price(price)
                    .attributes(new HashMap<>(attributes))
                    .build();
        }
        vehicle.setCategory(category);
        if (plate != null) {
            vehicle.addAttribute("plate", plate);
        }
        return vehicle;
    }


    private Map<String, Object> attributes = new HashMap<>();

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public String getId(){
        return id;
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public Vehicle copy() {
        return Vehicle.builder()
                .id(id)
                .category(category)
                .brand(brand)
                .model(model)
                .year(year)
                .plate(plate)
                .price(price)
                .attributes(new HashMap<>(attributes))
                .build();
    }
}