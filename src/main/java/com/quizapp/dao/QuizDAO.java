package com.quizapp.dao;

import com.quizapp.model.Quiz;
import com.quizapp.model.QuizAttempt;
import com.quizapp.util.DBConnectionUtil;
import com.quizapp.util.DemoDataStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class QuizDAO {

    public int createQuiz(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (user_id, is_revision, targeted_difficulty, generated_at) VALUES (?, ?, ?, ?)";
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, quiz.getUserId());
                statement.setBoolean(2, quiz.isRevisionQuiz());
                statement.setString(3, quiz.getTargetedDifficulty());
                statement.setTimestamp(4, Timestamp.valueOf(quiz.getGeneratedAt()));
                statement.executeUpdate();
                try (java.sql.ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
            return 0;
        } catch (SQLException ex) {
            return DemoDataStore.createQuiz(quiz);
        }
    }

    public void saveQuizAttempts(List<QuizAttempt> attempts) throws SQLException {
        String sql = "INSERT INTO quiz_attempts (quiz_id, user_id, question_id, selected_option, is_correct, response_time_seconds, attempted_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                for (QuizAttempt attempt : attempts) {
                    statement.setInt(1, attempt.getQuizId());
                    statement.setInt(2, attempt.getUserId());
                    statement.setInt(3, attempt.getQuestionId());
                    statement.setString(4, attempt.getSelectedOption());
                    statement.setBoolean(5, attempt.isCorrect());
                    statement.setLong(6, attempt.getResponseTimeSeconds());
                    statement.setTimestamp(7, Timestamp.valueOf(attempt.getAttemptedAt()));
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        } catch (SQLException ex) {
            DemoDataStore.saveQuizAttempts(attempts);
        }
    }
}
