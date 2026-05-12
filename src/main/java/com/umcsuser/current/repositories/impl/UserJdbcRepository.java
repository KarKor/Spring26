package com.umcsuser.current.repositories.impl;

import com.umcsuser.current.models.Role;
import com.umcsuser.current.models.User;
import com.umcsuser.current.repositories.UserRepository;
import com.umcsuser.current.db.JdbcConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserJdbcRepository implements UserRepository {

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = User.builder()
                        .ID(rs.getString("id"))
                        .login(rs.getString("login"))
                        .passwordHash(rs.getString("password"))
                        .role(Role.valueOf(rs.getString("role")))
                        .build();
                list.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        }
        return list;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = User.builder()
                            .ID(rs.getString("id"))
                            .login(rs.getString("login"))
                            .passwordHash(rs.getString("password"))
                            .role(Role.valueOf(rs.getString("role")))
                            .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading user", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = User.builder()
                            .ID(rs.getString("id"))
                            .login(rs.getString("login"))
                            .passwordHash(rs.getString("password"))
                            .role(Role.valueOf(rs.getString("role")))
                            .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading user by login", e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        boolean exists = false;

        if (user.getID() == null || user.getID().isBlank()) {
            user.setID(UUID.randomUUID().toString());
        } else {
            String checkSql = "SELECT 1 FROM users WHERE id = ?";
            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, user.getID());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    exists = rs.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred while checking if user exists", e);
            }
        }

        String sql;
        if (exists) {
            sql = "UPDATE users SET login = ?, password = ?, role = ? WHERE id = ?";
        } else {
            sql = "INSERT INTO users (login, password, role, id) VALUES (?, ?, ?, ?)";
        }

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, String.valueOf(user.getRole()));
            stmt.setString(4, user.getID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving user", e);
        }
        return user;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting user", e);
        }
    }
}