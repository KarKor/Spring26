package com.umcsuser.current.services;

import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.models.VehicleCategoryConfig;

import java.util.Map;

public class VehicleValidator {

    private final VehicleCategoryConfigService configService;

    public VehicleValidator(VehicleCategoryConfigService configService) {
        this.configService = configService;
    }

    public void validate(Vehicle vehicle) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle cannot be null.");

        validateBaseFields(vehicle);
        validateAttributes(vehicle.getAttributes(), configService.getByCategory(vehicle.getCategory()));
    }

    private void validateBaseFields(Vehicle vehicle) {
        requireNonBlank(vehicle.getCategory(), "Category is required.");
        requireNonBlank(vehicle.getBrand(), "Brand is required.");
        requireNonBlank(vehicle.getModel(), "Model is required.");
        requireNonBlank(vehicle.getPlate(), "Plate is required.");

        if (vehicle.getYear() <= 0) throw new IllegalArgumentException("Year cannot be negative.");
        if (vehicle.getPrice() < 0) throw new IllegalArgumentException("Price cannot be negative.");
    }

    private void validateAttributes(Map<String, Object> actualAttributes, VehicleCategoryConfig config) {
        Map<String, String> expectedAttributes = config.getAttributes();
        for (String actualName : actualAttributes.keySet()) {
            if (!expectedAttributes.containsKey(actualName)) {
                throw new IllegalArgumentException("Unsupported attribute for category "
                        + config.getCategory() + ": " + actualName);
            }
        }

        expectedAttributes.forEach((attrName, expectedType) -> {
            Object value = actualAttributes.get(attrName);
            if (value == null) {
                throw new IllegalArgumentException("Missing attribute: " + attrName);
            }
            if (expectedType.equalsIgnoreCase("string") && value instanceof String str) {
                requireNonBlank(str, "Attribute " + attrName + " cannot be empty.");
            }

            boolean isValidType = switch (expectedType.toLowerCase()) {
                case "string" -> value instanceof String;
                case "number" -> value instanceof Number;
                case "boolean" -> value instanceof Boolean;
                case "integer" -> value instanceof Integer;
                default -> throw new IllegalArgumentException("Unsupported type in config: " + expectedType);
            };
            if (!isValidType) {
                throw new IllegalArgumentException("Attribute " + attrName + " must be of type " + expectedType + ".");
            }
        });
    }
    private void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}