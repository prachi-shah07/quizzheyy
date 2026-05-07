package com.quizapp.dao;

import com.quizapp.model.Performance;
import com.quizapp.util.DBConnectionUtil;
import com.quizapp.util.DemoDataStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PerformanceDAO {

    public List<Performance> getPerformanceByUserId(int userId) throws SQLException {
        String sql = "SELECT user_id, topic, accuracy, speed, consistency, composite_score, average_response_time, attempts, last_updated "
                + "FROM performance WHERE user_id = ? ORDER BY composite_score ASC";
        List<Performance> performanceList = new ArrayList<>();
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        performanceList.add(mapPerformance(resultSet));
                    }
                }
            }
            return performanceList;
        } catch (SQLException ex) {
            return DemoDataStore.getPerformanceByUserId(userId);
        }
    }

    public double getOverallAccuracy(int userId) throws SQLException {
        String sql = "SELECT COALESCE(AVG(accuracy), 0) AS overall_accuracy FROM performance WHERE user_id = ?";
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getDouble("overall_accuracy");
                    }
                }
            }
            return 0.0;
        } catch (SQLException ex) {
            return DemoDataStore.getOverallAccuracy(userId);
        }
    }

    public void refreshPerformanceForUser(int userId) throws SQLException {
        String deleteSql = "DELETE FROM performance WHERE user_id = ?";
        String aggregateSql = "SELECT q.topic, COUNT(*) AS attempts, "
                + "AVG(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) AS accuracy, "
                + "AVG(CASE WHEN qa.response_time_seconds >= 30 THEN 0 ELSE GREATEST(0, 1 - (qa.response_time_seconds / 30.0)) END) AS speed_score, "
                + "CASE WHEN COUNT(*) = 1 "
                + "THEN AVG(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) "
                + "ELSE GREATEST(0, 1 - STDDEV_POP(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END)) END AS consistency_score, "
                + "AVG(qa.response_time_seconds) AS avg_response_time "
                + "FROM quiz_attempts qa "
                + "INNER JOIN questions q ON q.id = qa.question_id "
                + "WHERE qa.user_id = ? "
                + "GROUP BY q.topic";
        String upsertSql = "INSERT INTO performance (user_id, topic, accuracy, speed, consistency, composite_score, average_response_time, attempts, last_updated) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE accuracy = VALUES(accuracy), speed = VALUES(speed), consistency = VALUES(consistency), "
                + "composite_score = VALUES(composite_score), average_response_time = VALUES(average_response_time), attempts = VALUES(attempts), last_updated = VALUES(last_updated)";

        try {
            try (Connection connection = DBConnectionUtil.getConnection()) {
                connection.setAutoCommit(false);

                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                    deleteStatement.setInt(1, userId);
                    deleteStatement.executeUpdate();
                }

                try (PreparedStatement aggregateStatement = connection.prepareStatement(aggregateSql);
                     PreparedStatement upsertStatement = connection.prepareStatement(upsertSql)) {
                    aggregateStatement.setInt(1, userId);
                    try (ResultSet resultSet = aggregateStatement.executeQuery()) {
                        while (resultSet.next()) {
                            double accuracy = resultSet.getDouble("accuracy");
                            double speed = resultSet.getDouble("speed_score");
                            double consistency = resultSet.getDouble("consistency_score");
                            double compositeScore = (accuracy * 0.6) + (speed * 0.2) + (consistency * 0.2);

                            upsertStatement.setInt(1, userId);
                            upsertStatement.setString(2, resultSet.getString("topic"));
                            upsertStatement.setDouble(3, accuracy);
                            upsertStatement.setDouble(4, speed);
                            upsertStatement.setDouble(5, consistency);
                            upsertStatement.setDouble(6, compositeScore);
                            upsertStatement.setDouble(7, resultSet.getDouble("avg_response_time"));
                            upsertStatement.setInt(8, resultSet.getInt("attempts"));
                            upsertStatement.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
                            upsertStatement.addBatch();
                        }
                    }
                    upsertStatement.executeBatch();
                }

                connection.commit();
            }
        } catch (SQLException ex) {
            DemoDataStore.refreshPerformanceForUser(userId);
        }
    }

    private Performance mapPerformance(ResultSet resultSet) throws SQLException {
        Performance performance = new Performance();
        performance.setUserId(resultSet.getInt("user_id"));
        performance.setTopic(resultSet.getString("topic"));
        performance.setAccuracy(resultSet.getDouble("accuracy"));
        performance.setSpeed(resultSet.getDouble("speed"));
        performance.setConsistency(resultSet.getDouble("consistency"));
        performance.setCompositeScore(resultSet.getDouble("composite_score"));
        performance.setAverageResponseTime(resultSet.getDouble("average_response_time"));
        performance.setAttempts(resultSet.getInt("attempts"));
        Timestamp lastUpdated = resultSet.getTimestamp("last_updated");
        if (lastUpdated != null) {
            performance.setLastUpdated(lastUpdated.toLocalDateTime());
        }
        return performance;
    }
}
