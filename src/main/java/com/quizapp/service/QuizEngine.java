package com.quizapp.service;

import com.quizapp.model.Quiz;
import com.quizapp.model.QuizEvaluationResult;
import com.quizapp.model.User;

import java.util.Map;

public abstract class QuizEngine {

    protected Quiz currentQuiz;
    protected User currentUser;
    protected Map<Integer, String> submittedAnswers;
    protected Map<Integer, Long> responseTimes;

    public abstract Quiz generateQuiz(User user) throws Exception;

    public abstract QuizEvaluationResult evaluateQuiz() throws Exception;

    public void prepareEvaluation(Quiz quiz, User user, Map<Integer, String> submittedAnswers, Map<Integer, Long> responseTimes) {
        this.currentQuiz = quiz;
        this.currentUser = user;
        this.submittedAnswers = submittedAnswers;
        this.responseTimes = responseTimes;
    }
}
