package com.umcsuser.current.repositories.impl;

import com.umcsuser.current.models.Role;
import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.UserRepository;
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
public class UserJdbcRepository implements UserRepository {

    private final DataSource dataSource;

    public UserJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return list;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading user", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading user by login", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }

        boolean exists = false;
        String checkSql = "SELECT 1 FROM users WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try {
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, user.getId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    exists = rs.next();
                }
            }

            String sql;
            if (exists) {
                sql = "UPDATE users SET login = ?, password_hash = ?, role = ? WHERE id = ?";
            } else {
                sql = "INSERT INTO users (login, password_hash, role, id) VALUES (?, ?, ?, ?)";
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, user.getLogin());
                stmt.setString(2, user.getPasswordHash());
                stmt.setString(3, String.valueOf(user.getRole()));
                stmt.setString(4, user.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving user", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return user;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting user", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getString("id"))
                .login(rs.getString("login"))
                .passwordHash(rs.getString("password_hash"))
                .role(Role.valueOf(rs.getString("role")))
                .build();
    }
}