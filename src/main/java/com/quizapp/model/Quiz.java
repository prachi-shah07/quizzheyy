package com.quizapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private int id;
    private int userId;
    private List<Question> questions = new ArrayList<>();
    private LocalDateTime generatedAt;
    private boolean revisionQuiz;
    private int perQuestionTimeLimitSeconds;
    private String targetedDifficulty;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public boolean isRevisionQuiz() {
        return revisionQuiz;
    }

    public void setRevisionQuiz(boolean revisionQuiz) {
        this.revisionQuiz = revisionQuiz;
    }

    public int getPerQuestionTimeLimitSeconds() {
        return perQuestionTimeLimitSeconds;
    }

    public void setPerQuestionTimeLimitSeconds(int perQuestionTimeLimitSeconds) {
        this.perQuestionTimeLimitSeconds = perQuestionTimeLimitSeconds;
    }

    public String getTargetedDifficulty() {
        return targetedDifficulty;
    }

    public void setTargetedDifficulty(String targetedDifficulty) {
        this.targetedDifficulty = targetedDifficulty;
    }
}
