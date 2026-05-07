package com.quizapp.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdaptiveQuizSession {

    private Quiz quiz;
    private User user;
    private List<Question> candidateQuestions = new ArrayList<>();
    private Set<Integer> usedQuestionIds = new LinkedHashSet<>();
    private List<String> weakTopics = new ArrayList<>();
    private List<String> mediumTopics = new ArrayList<>();
    private List<String> strongTopics = new ArrayList<>();
    private Map<Integer, String> submittedAnswers = new LinkedHashMap<>();
    private Map<Integer, Long> responseTimes = new LinkedHashMap<>();
    private Map<Integer, Boolean> correctnessMap = new LinkedHashMap<>();
    private int currentIndex;
    private int currentDifficulty;
    private int correctStreak;
    private int maxQuestions;
    private boolean revisionMode;
    private String lastAdaptiveMessage;

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Question> getCandidateQuestions() {
        return candidateQuestions;
    }

    public void setCandidateQuestions(List<Question> candidateQuestions) {
        this.candidateQuestions = candidateQuestions;
    }

    public Set<Integer> getUsedQuestionIds() {
        return usedQuestionIds;
    }

    public void setUsedQuestionIds(Set<Integer> usedQuestionIds) {
        this.usedQuestionIds = usedQuestionIds;
    }

    public List<String> getWeakTopics() {
        return weakTopics;
    }

    public void setWeakTopics(List<String> weakTopics) {
        this.weakTopics = weakTopics;
    }

    public List<String> getMediumTopics() {
        return mediumTopics;
    }

    public void setMediumTopics(List<String> mediumTopics) {
        this.mediumTopics = mediumTopics;
    }

    public List<String> getStrongTopics() {
        return strongTopics;
    }

    public void setStrongTopics(List<String> strongTopics) {
        this.strongTopics = strongTopics;
    }

    public Map<Integer, String> getSubmittedAnswers() {
        return submittedAnswers;
    }

    public void setSubmittedAnswers(Map<Integer, String> submittedAnswers) {
        this.submittedAnswers = submittedAnswers;
    }

    public Map<Integer, Long> getResponseTimes() {
        return responseTimes;
    }

    public void setResponseTimes(Map<Integer, Long> responseTimes) {
        this.responseTimes = responseTimes;
    }

    public Map<Integer, Boolean> getCorrectnessMap() {
        return correctnessMap;
    }

    public void setCorrectnessMap(Map<Integer, Boolean> correctnessMap) {
        this.correctnessMap = correctnessMap;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(int currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public int getCorrectStreak() {
        return correctStreak;
    }

    public void setCorrectStreak(int correctStreak) {
        this.correctStreak = correctStreak;
    }

    public int getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(int maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public boolean isRevisionMode() {
        return revisionMode;
    }

    public void setRevisionMode(boolean revisionMode) {
        this.revisionMode = revisionMode;
    }

    public String getLastAdaptiveMessage() {
        return lastAdaptiveMessage;
    }

    public void setLastAdaptiveMessage(String lastAdaptiveMessage) {
        this.lastAdaptiveMessage = lastAdaptiveMessage;
    }

    public Question getCurrentQuestion() {
        if (quiz == null || quiz.getQuestions().isEmpty()) {
            return null;
        }
        return quiz.getQuestions().get(currentIndex);
    }

    public boolean isCurrentQuestionAnswered() {
        Question question = getCurrentQuestion();
        return question != null && submittedAnswers.containsKey(question.getId());
    }

    public boolean isViewingHistory() {
        return quiz != null && currentIndex < quiz.getQuestions().size() - 1;
    }
}
