package com.quizapp.dao;

import com.quizapp.model.User;
import com.quizapp.util.DBConnectionUtil;
import com.quizapp.util.DemoDataStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAO {

    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, password_hash, created_at) VALUES (?, ?, ?, NOW())";
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getFullName());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPasswordHash());
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                    }
                }
            }
            return user;
        } catch (SQLException ex) {
            return DemoDataStore.createUser(user);
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, full_name, email, password_hash, created_at FROM users WHERE email = ?";
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapUser(resultSet);
                    }
                }
            }
            return null;
        } catch (SQLException ex) {
            return DemoDataStore.findUserByEmail(email);
        }
    }

    public User findById(int userId) throws SQLException {
        String sql = "SELECT id, full_name, email, password_hash, created_at FROM users WHERE id = ?";
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapUser(resultSet);
                    }
                }
            }
            return null;
        } catch (SQLException ex) {
            return DemoDataStore.findUserById(userId);
        }
    }

    public User authenticate(String email, String passwordHash) throws SQLException {
        String sql = "SELECT id, full_name, email, password_hash, created_at FROM users WHERE email = ? AND password_hash = ?";
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                statement.setString(2, passwordHash);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapUser(resultSet);
                    }
                }
            }
            return null;
        } catch (SQLException ex) {
            return DemoDataStore.authenticate(email, passwordHash);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        return user;
    }
}
