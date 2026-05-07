package com.quizapp.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuizEvaluationResult {

    private int correctAnswers;
    private int totalQuestions;
    private double percentageScore;
    private double averageResponseTime;
    private String adaptiveMessage;
    private String difficultyRecommendation;
    private List<Question> incorrectQuestions = new ArrayList<>();
    private Map<Integer, String> submittedAnswers = new LinkedHashMap<>();
    private Map<Integer, Boolean> correctnessMap = new LinkedHashMap<>();
    private Map<String, Double> topicAccuracy = new LinkedHashMap<>();

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public double getPercentageScore() {
        return percentageScore;
    }

    public void setPercentageScore(double percentageScore) {
        this.percentageScore = percentageScore;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public String getAdaptiveMessage() {
        return adaptiveMessage;
    }

    public void setAdaptiveMessage(String adaptiveMessage) {
        this.adaptiveMessage = adaptiveMessage;
    }

    public String getDifficultyRecommendation() {
        return difficultyRecommendation;
    }

    public void setDifficultyRecommendation(String difficultyRecommendation) {
        this.difficultyRecommendation = difficultyRecommendation;
    }

    public List<Question> getIncorrectQuestions() {
        return incorrectQuestions;
    }

    public void setIncorrectQuestions(List<Question> incorrectQuestions) {
        this.incorrectQuestions = incorrectQuestions;
    }

    public Map<Integer, String> getSubmittedAnswers() {
        return submittedAnswers;
    }

    public void setSubmittedAnswers(Map<Integer, String> submittedAnswers) {
        this.submittedAnswers = submittedAnswers;
    }

    public Map<Integer, Boolean> getCorrectnessMap() {
        return correctnessMap;
    }

    public void setCorrectnessMap(Map<Integer, Boolean> correctnessMap) {
        this.correctnessMap = correctnessMap;
    }

    public Map<String, Double> getTopicAccuracy() {
        return topicAccuracy;
    }

    public void setTopicAccuracy(Map<String, Double> topicAccuracy) {
        this.topicAccuracy = topicAccuracy;
    }
}
