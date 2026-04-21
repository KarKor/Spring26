import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.models.VehicleCategoryConfig;
import com.umcsuser.current.repositories.VehicleCategoryConfigRepository;
import com.umcsuser.current.services.VehicleCategoryConfigService;
import com.umcsuser.current.services.VehicleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VehicleValidatorTest {

    private VehicleValidator validator;

    @BeforeEach
    void setUp() {
        VehicleCategoryConfigRepository repo = new InMemoryVehicleCategoryConfigRepository(
                List.of(
                        VehicleCategoryConfig.builder()
                                .category("Car")
                                .attributes(Map.of("fuelType", "string"))
                                .build(),
                        VehicleCategoryConfig.builder()
                                .category("Motorcycle")
                                .attributes(Map.of("licence", "string"))
                                .build(),
                        VehicleCategoryConfig.builder()
                                .category("Bus")
                                .attributes(Map.of("seats", "integer"))
                                .build()
                )
        );

        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(repo);
        validator = new VehicleValidator(configService);
    }

    @Test
    void shouldPassForValidCar() {
        Vehicle vehicle = Vehicle.builder()
                .category("Car")
                .brand("Toyota")
                .model("Corolla")
                .year(2020)
                .plate("LU123")
                .price(100.0)
                .build();

        vehicle.addAttribute("fuelType", "hybrid");

        assertDoesNotThrow(() -> validator.validate(vehicle));
    }

    @Test
    void shouldPassForValidBus() {
        Vehicle vehicle = Vehicle.builder()
                .category("Bus")
                .model("Ford")
                .brand("Transit")
                .year(2021)
                .plate("LU222")
                .price(300.0)
                .build();

        vehicle.addAttribute("seats", 50);

        assertDoesNotThrow(() -> validator.validate(vehicle));
    }

    @Test
    void shouldThrowWhenFuelTypeIsMissingForCar() {
        Vehicle vehicle = Vehicle.builder()
                .category("Car")
                .brand("Toyota")
                .model("Corolla")
                .year(2020)
                .plate("LU123")
                .price(100.0)
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(vehicle)
        );

        assertEquals("Brak wymaganego atrybutu: fuelType", ex.getMessage());
    }

    @Test
    void shouldThrowWhenFuelTypeIsBlank() {
        Vehicle vehicle = Vehicle.builder()
                .category("Car")
                .brand("Toyota")
                .model("Corolla")
                .year(2020)
                .plate("LU123")
                .price(100.0)
                .build();

        vehicle.addAttribute("fuelType", "   ");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(vehicle)
        );

        assertEquals("Atrybut fuelType nie może być pusty.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenLicenceHasWrongTypeForMotorcycle() {
        Vehicle vehicle = Vehicle.builder()
                .category("Motorcycle")
                .brand("Honda")
                .model("CBR1000RR")
                .year(2022)
                .plate("LU333")
                .price(200.0)
                .build();

        vehicle.addAttribute("licence", true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(vehicle)
        );

        assertEquals("Atrybut licence musi być typu string.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenSeatsHasWrongTypeForBus() {
        Vehicle vehicle = Vehicle.builder()
                .category("Bus")
                .model("Ford")
                .brand("Transit")
                .year(2021)
                .plate("LU222")
                .price(500.0)
                .build();

        vehicle.addAttribute("seats", "dużo");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(vehicle)
        );

        assertEquals("Atrybut seats musi być typu integer.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAttributeIsNotSupportedForCategory() {
        Vehicle vehicle = Vehicle.builder()
                .category("Car")
                .brand("Toyota")
                .model("Corolla")
                .year(2020)
                .plate("LU123")
                .price(100.0)
                .build();

        vehicle.addAttribute("fuelType", "petrol");
        vehicle.addAttribute("seats", 5);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(vehicle)
        );

        assertEquals("Nieobsługiwany atrybut dla kategorii Car: seats", ex.getMessage());
    }

    @Test
    void shouldThrowWhenPriceIsNegative() {
        Vehicle vehicle = Vehicle.builder()
                .category("Car")
                .brand("Toyota")
                .model("Corolla")
                .year(2020)
                .plate("LU123")
                .price(-10.0)
                .build();

        vehicle.addAttribute("fuelType", "hybrid");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(vehicle)
        );

        assertEquals("Cena nie może być ujemna.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenYearIsNotPositive() {
        Vehicle vehicle = Vehicle.builder()
                .category("Car")
                .brand("Toyota")
                .model("Corolla")
                .year(0)
                .plate("LU12345")
                .price(100.0)
                .build();

        vehicle.addAttribute("fuelType", "petrol");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(vehicle)
        );

        assertEquals("Rok musi być dodatni.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenVehicleIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(null)
        );

        assertEquals("Pojazd nie może być nullem.", ex.getMessage());
    }

    private static class InMemoryVehicleCategoryConfigRepository implements VehicleCategoryConfigRepository {

        private final List<VehicleCategoryConfig> configs;

        private InMemoryVehicleCategoryConfigRepository(List<VehicleCategoryConfig> configs) {
            this.configs = configs;
        }

        @Override
        public List<VehicleCategoryConfig> findAll() {
            return configs;
        }

        @Override
        public Optional<VehicleCategoryConfig> findByCategory(String category) {
            return configs.stream()
                    .filter(c -> c.getCategory() != null)
                    .filter(c -> c.getCategory().equalsIgnoreCase(category))
                    .findFirst();
        }
    }
}