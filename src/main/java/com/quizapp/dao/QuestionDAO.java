package com.quizapp.dao;

import com.quizapp.model.Question;
import com.quizapp.util.DBConnectionUtil;
import com.quizapp.util.DemoDataStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public List<Question> findAllQuestions() throws SQLException {
        String sql = "SELECT id, topic, difficulty_level, question_text, option_a, option_b, option_c, option_d, correct_option, explanation FROM questions";
        List<Question> questions = new ArrayList<>();
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    questions.add(mapQuestion(resultSet));
                }
            }
            return questions;
        } catch (SQLException ex) {
            return DemoDataStore.getAllQuestions();
        }
    }

    public List<Question> findIncorrectQuestionsForUser(int userId, int limit) throws SQLException {
        String sql = "SELECT q.id, q.topic, q.difficulty_level, q.question_text, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_option, q.explanation "
                + "FROM questions q "
                + "INNER JOIN quiz_attempts qa ON qa.question_id = q.id "
                + "WHERE qa.user_id = ? AND qa.is_correct = 0 "
                + "GROUP BY q.id, q.topic, q.difficulty_level, q.question_text, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_option, q.explanation "
                + "ORDER BY MAX(qa.attempted_at) DESC "
                + "LIMIT ?";
        List<Question> questions = new ArrayList<>();
        try {
            try (Connection connection = DBConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.setInt(2, limit);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        questions.add(mapQuestion(resultSet));
                    }
                }
            }
            return questions;
        } catch (SQLException ex) {
            return DemoDataStore.getIncorrectQuestionsForUser(userId, limit);
        }
    }

    private Question mapQuestion(ResultSet resultSet) throws SQLException {
        Question question = new Question();
        question.setId(resultSet.getInt("id"));
        question.setTopic(resultSet.getString("topic"));
        question.setDifficultyLevel(resultSet.getInt("difficulty_level"));
        question.setQuestionText(resultSet.getString("question_text"));
        question.setOptionA(resultSet.getString("option_a"));
        question.setOptionB(resultSet.getString("option_b"));
        question.setOptionC(resultSet.getString("option_c"));
        question.setOptionD(resultSet.getString("option_d"));
        question.setCorrectOption(resultSet.getString("correct_option"));
        question.setExplanation(resultSet.getString("explanation"));
        return question;
    }
}
