package com.umcsuser.current.repositories.impl;

import com.umcsuser.current.models.Rental;
import com.umcsuser.current.models.User;
import com.umcsuser.current.models.Vehicle;
import com.umcsuser.current.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jdbc")
public class RentalJdbcRepository implements RentalRepository {

    private final DataSource dataSource;

    public RentalJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> list = new ArrayList<>();

        String sql = """
            SELECT r.*, v.brand, v.model, v.year, v.plate, u.login, u.role
            FROM rental r
            JOIN vehicle v ON r.vehicle_id = v.id
            JOIN users u ON r.user_id = u.id
            """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return list;
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        return Rental.builder()
                .id(rs.getString("id"))
                .rentDateTime(rs.getString("rent_date"))
                .returnDateTime(rs.getString("return_date"))
                .vehicle(Vehicle.builder()
                        .id(rs.getString("vehicle_id"))
                        .brand(rs.getString("brand"))
                        .model(rs.getString("model"))
                        .year(rs.getInt("year"))
                        .plate(rs.getString("plate"))
                        .build())
                .user(User.builder()
                        .id(rs.getString("user_id"))
                        .login(rs.getString("login"))
                        .build())
                .build();
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT * FROM rental WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rental", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        }

        boolean exists = false;
        String checkSql = "SELECT 1 FROM rental WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try {
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, rental.getId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    exists = rs.next();
                }
            }

            String sql;
            if (exists) {
                sql = "UPDATE rental SET vehicle_id = ?, user_id = ?, rent_date = ?, return_date = ? WHERE id = ?";
            } else {
                sql = "INSERT INTO rental (vehicle_id, user_id, rent_date, return_date, id) VALUES (?, ?, ?, ?, ?)";
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, rental.getVehicle().getId());
                stmt.setString(2, rental.getUser().getId());
                stmt.setString(3, rental.getRentDateTime());
                stmt.setString(4, rental.getReturnDateTime());
                stmt.setString(5, rental.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving rental", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while searching for active rental", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }


}