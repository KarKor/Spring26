package com.umcsuser.current.models;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class VehicleCategoryConfig {
    private String category;
    private Map<String, String> attributes;
}