package com.umcsuser.current.services;

import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.models.VehicleCategoryConfig;
import com.umcsuser.current.services.VehicleCategoryConfigService;

import java.util.Map;
import java.util.Optional;

public class VehicleValidator {
    private final VehicleCategoryConfigService configService;

    public VehicleValidator(VehicleCategoryConfigService configService) {
        this.configService = configService;
    }

    public void validate(Vehicle vehicle) {
        if (vehicle.getBrand() == null || vehicle.getBrand().isBlank()) throw new IllegalArgumentException("Brand is required.");
        if (vehicle.getModel() == null || vehicle.getModel().isBlank()) throw new IllegalArgumentException("Model is required.");
        if (vehicle.getPlate() == null || vehicle.getPlate().isBlank()) throw new IllegalArgumentException("Plate is required.");
        if (vehicle.getYear() <= 1900) throw new IllegalArgumentException("Invalid production year.");
        if (vehicle.getPrice() <= 0) throw new IllegalArgumentException("Price must be higher than 0.");
        if (vehicle.getCategory() == null || vehicle.getCategory().isBlank()) throw new IllegalArgumentException("Category is required.");

        Optional<VehicleCategoryConfig> configOpt = configService.getConfigForCategory(vehicle.getCategory());
        if (configOpt.isEmpty()) {
            throw new IllegalArgumentException("Unknown vehicle category: " + vehicle.getCategory());
        }

        VehicleCategoryConfig config = configOpt.get();
        Map<String, String> expectedAttributes = config.getAttributes();
        Map<String, Object> actualAttributes = vehicle.getAttributes();

        if (expectedAttributes != null) {
            for (Map.Entry<String, String> entry : expectedAttributes.entrySet()) {
                String attrName = entry.getKey();
                String attrType = entry.getValue();

                if (!actualAttributes.containsKey(attrName)) {
                    throw new IllegalArgumentException("A required attribute is missing: " + attrName + " for category " + vehicle.getCategory());
                }

                Object value = actualAttributes.get(attrName);
                validateType(attrName, value, attrType);
            }
        }
    }

    private void validateType(String attrName, Object value, String expectedType) {
        if (value == null) throw new IllegalArgumentException("Attribute " + attrName + " cannot be empty.");

        try {
            switch (expectedType.toLowerCase()) {
                case "integer":
                    Integer.parseInt(value.toString());
                    break;
                case "double":
                    Double.parseDouble(value.toString());
                    break;
                case "string":
                    break;
                case "boolean":
                    if (!value.toString().equalsIgnoreCase("true") && !value.toString().equalsIgnoreCase("false")) {
                        throw new IllegalArgumentException("Attribute " + attrName + " must be of type boolean (true/false).");
                    }
                    break;
                default:
                    break;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Attribute " + attrName + " must be of type " + expectedType);
        }
    }
}