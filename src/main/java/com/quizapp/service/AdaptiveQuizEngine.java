package com.quizapp.service;

import com.quizapp.dao.PerformanceDAO;
import com.quizapp.dao.QuestionDAO;
import com.quizapp.dao.QuizDAO;
import com.quizapp.exception.InvalidAnswerException;
import com.quizapp.exception.QuizNotFoundException;
import com.quizapp.model.AdaptiveQuizSession;
import com.quizapp.model.Performance;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.QuizAttempt;
import com.quizapp.model.QuizEvaluationResult;
import com.quizapp.model.SkillProfile;
import com.quizapp.model.User;
import com.quizapp.util.AppConstants;
import com.quizapp.util.SkillProfileUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AdaptiveQuizEngine extends QuizEngine {

    private final QuestionDAO questionDAO;
    private final PerformanceDAO performanceDAO;
    private final QuizDAO quizDAO;

    public AdaptiveQuizEngine() {
        this.questionDAO = new QuestionDAO();
        this.performanceDAO = new PerformanceDAO();
        this.quizDAO = new QuizDAO();
    }

    public AdaptiveQuizSession startAdaptiveQuiz(User user, boolean revisionMode) throws Exception {
        List<Question> questionPool = revisionMode
                ? questionDAO.findIncorrectQuestionsForUser(user.getId(), AppConstants.REVISION_QUIZ_SIZE)
                : questionDAO.findAllQuestions();

        if (questionPool.isEmpty()) {
            questionPool = questionDAO.findAllQuestions();
            revisionMode = false;
        }

        List<Performance> performanceList = performanceDAO.getPerformanceByUserId(user.getId());
        SkillProfile skillProfile = SkillProfileUtil.buildSkillProfile(performanceList);
        user.setSkillProfile(skillProfile);

        int preferredDifficulty = determineDifficulty(skillProfile.getAverageCompositeScore());
        Quiz quiz = new Quiz();
        quiz.setUserId(user.getId());
        quiz.setGeneratedAt(LocalDateTime.now());
        quiz.setRevisionQuiz(revisionMode);
        quiz.setPerQuestionTimeLimitSeconds(AppConstants.PER_QUESTION_TIME_LIMIT_SECONDS);
        quiz.setTargetedDifficulty(revisionMode ? "Revision" : difficultyLabel(preferredDifficulty));
        quiz.setId(quizDAO.createQuiz(quiz));

        AdaptiveQuizSession session = new AdaptiveQuizSession();
        session.setUser(user);
        session.setQuiz(quiz);
        session.setCandidateQuestions(new ArrayList<>(questionPool));
        session.setCurrentDifficulty(preferredDifficulty);
        session.setCorrectStreak(0);
        session.setCurrentIndex(0);
        session.setRevisionMode(revisionMode);
        session.setMaxQuestions(revisionMode
                ? Math.min(AppConstants.REVISION_QUIZ_SIZE, questionPool.size())
                : Math.min(AppConstants.DEFAULT_QUIZ_SIZE, questionPool.size()));

        List<String> weakTopics = new ArrayList<>(skillProfile.getWeakTopics());
        List<String> mediumTopics = new ArrayList<>(skillProfile.getMediumTopics());
        List<String> strongTopics = new ArrayList<>(skillProfile.getStrongTopics());
        if (performanceList.isEmpty()) {
            mediumTopics = questionPool.stream().map(Question::getTopic).distinct().collect(Collectors.toList());
        }
        session.setWeakTopics(weakTopics);
        session.setMediumTopics(mediumTopics);
        session.setStrongTopics(strongTopics);

        Question firstQuestion = chooseNextQuestion(session);
        if (firstQuestion != null) {
            session.getQuiz().getQuestions().add(firstQuestion);
            session.getUsedQuestionIds().add(firstQuestion.getId());
        }

        return session;
    }

    public void recordCurrentAnswer(AdaptiveQuizSession session, String submittedAnswer, long responseTimeSeconds) throws Exception {
        Question currentQuestion = session.getCurrentQuestion();
        if (currentQuestion == null || session.isCurrentQuestionAnswered()) {
            return;
        }

        if (submittedAnswer != null && !submittedAnswer.isBlank() && !submittedAnswer.matches("[ABCD]")) {
            throw new InvalidAnswerException("Invalid option received for question ID " + currentQuestion.getId());
        }

        long normalizedResponseTime = Math.max(0L, Math.min(responseTimeSeconds, currentQuestion == null
                ? AppConstants.PER_QUESTION_TIME_LIMIT_SECONDS
                : session.getQuiz().getPerQuestionTimeLimitSeconds()));
        String storedAnswer = (submittedAnswer == null || submittedAnswer.isBlank()) ? "Not Answered" : submittedAnswer;
        boolean isCorrect = currentQuestion.getCorrectOption().equalsIgnoreCase(submittedAnswer);

        session.getSubmittedAnswers().put(currentQuestion.getId(), storedAnswer);
        session.getResponseTimes().put(currentQuestion.getId(), normalizedResponseTime);
        session.getCorrectnessMap().put(currentQuestion.getId(), isCorrect);

        int priorDifficulty = session.getCurrentDifficulty();
        if (isCorrect) {
            session.setCorrectStreak(session.getCorrectStreak() + 1);
            if (session.getCorrectStreak() >= 4) {
                session.setCurrentDifficulty(Math.min(3, priorDifficulty + 1));
                session.setLastAdaptiveMessage("You answered 4 in a row correctly, so the next question difficulty increased immediately.");
            } else {
                session.setLastAdaptiveMessage("Correct answer. The system kept your current difficulty for the next question.");
            }
        } else {
            session.setCorrectStreak(0);
            session.setCurrentDifficulty(Math.max(1, priorDifficulty - 1));
            session.setLastAdaptiveMessage("Incorrect answer. The next question difficulty decreased immediately to support recovery.");
        }
    }

    public boolean appendNextQuestion(AdaptiveQuizSession session) {
        if (session.getQuiz().getQuestions().size() >= session.getMaxQuestions()) {
            return false;
        }

        Question nextQuestion = chooseNextQuestion(session);
        if (nextQuestion == null) {
            return false;
        }

        session.getQuiz().getQuestions().add(nextQuestion);
        session.getUsedQuestionIds().add(nextQuestion.getId());
        session.setCurrentIndex(session.getQuiz().getQuestions().size() - 1);
        return true;
    }

    public void movePrevious(AdaptiveQuizSession session) {
        session.setCurrentIndex(Math.max(0, session.getCurrentIndex() - 1));
    }

    public void moveNext(AdaptiveQuizSession session) {
        session.setCurrentIndex(Math.min(session.getQuiz().getQuestions().size() - 1, session.getCurrentIndex() + 1));
    }

    @Override
    public Quiz generateQuiz(User user) throws Exception {
        List<Question> allQuestions = questionDAO.findAllQuestions();
        List<Performance> performanceList = performanceDAO.getPerformanceByUserId(user.getId());
        SkillProfile skillProfile = SkillProfileUtil.buildSkillProfile(performanceList);
        user.setSkillProfile(skillProfile);

        int preferredDifficulty = determineDifficulty(skillProfile.getAverageCompositeScore());
        Set<Integer> usedQuestionIds = new HashSet<>();

        Quiz quiz = new Quiz();
        quiz.setUserId(user.getId());
        quiz.setGeneratedAt(LocalDateTime.now());
        quiz.setRevisionQuiz(false);
        quiz.setPerQuestionTimeLimitSeconds(AppConstants.PER_QUESTION_TIME_LIMIT_SECONDS);
        quiz.setTargetedDifficulty(difficultyLabel(preferredDifficulty));

        List<String> weakTopics = skillProfile.getWeakTopics();
        List<String> mediumTopics = skillProfile.getMediumTopics();
        List<String> strongTopics = skillProfile.getStrongTopics();

        if (performanceList.isEmpty()) {
            mediumTopics = allQuestions.stream()
                    .map(Question::getTopic)
                    .distinct()
                    .collect(Collectors.toList());
        }

        List<Question> selectedQuestions = new ArrayList<>();
        selectedQuestions.addAll(selectByCategory(allQuestions, weakTopics, 5, preferredDifficulty, usedQuestionIds));
        selectedQuestions.addAll(selectByCategory(allQuestions, mediumTopics, 3, preferredDifficulty, usedQuestionIds));
        selectedQuestions.addAll(selectByCategory(allQuestions, strongTopics, 2, preferredDifficulty, usedQuestionIds));

        if (selectedQuestions.size() < AppConstants.DEFAULT_QUIZ_SIZE) {
            selectedQuestions.addAll(fillRemainingQuestions(allQuestions, AppConstants.DEFAULT_QUIZ_SIZE - selectedQuestions.size(), preferredDifficulty, usedQuestionIds));
        }

        quiz.setQuestions(selectedQuestions);
        quiz.setId(quizDAO.createQuiz(quiz));
        this.currentQuiz = quiz;
        this.currentUser = user;
        return quiz;
    }

    public Quiz generateRevisionQuiz(User user) throws Exception {
        List<Question> revisionQuestions = questionDAO.findIncorrectQuestionsForUser(user.getId(), AppConstants.REVISION_QUIZ_SIZE);
        if (revisionQuestions.isEmpty()) {
            return generateQuiz(user);
        }

        Quiz quiz = new Quiz();
        quiz.setUserId(user.getId());
        quiz.setGeneratedAt(LocalDateTime.now());
        quiz.setRevisionQuiz(true);
        quiz.setPerQuestionTimeLimitSeconds(AppConstants.PER_QUESTION_TIME_LIMIT_SECONDS);
        quiz.setTargetedDifficulty("Revision");
        quiz.setQuestions(revisionQuestions);
        quiz.setId(quizDAO.createQuiz(quiz));
        this.currentQuiz = quiz;
        this.currentUser = user;
        return quiz;
    }

    @Override
    public QuizEvaluationResult evaluateQuiz() throws Exception {
        if (currentQuiz == null || currentUser == null) {
            throw new QuizNotFoundException("No active quiz was found in session.");
        }

        QuizEvaluationResult result = new QuizEvaluationResult();
        List<QuizAttempt> attempts = new ArrayList<>();
        List<Question> incorrectQuestions = new ArrayList<>();
        Map<String, int[]> topicStats = new HashMap<>();
        long totalResponseTime = 0;
        int correctAnswers = 0;

        for (Question question : currentQuiz.getQuestions()) {
            String submittedAnswer = submittedAnswers.get(question.getId());
            boolean answered = submittedAnswer != null && submittedAnswer.matches("[ABCD]");
            if (submittedAnswer != null && !answered && !"Not Answered".equalsIgnoreCase(submittedAnswer)) {
                throw new InvalidAnswerException("Invalid option received for question ID " + question.getId());
            }

            boolean isCorrect = answered && question.getCorrectOption().equalsIgnoreCase(submittedAnswer);
            long responseTime = responseTimes.getOrDefault(question.getId(), (long) currentQuiz.getPerQuestionTimeLimitSeconds());
            totalResponseTime += responseTime;

            if (isCorrect) {
                correctAnswers++;
            } else {
                incorrectQuestions.add(question);
            }

            int[] stats = topicStats.computeIfAbsent(question.getTopic(), key -> new int[2]);
            stats[1]++;
            if (isCorrect) {
                stats[0]++;
            }

            QuizAttempt attempt = new QuizAttempt();
            attempt.setQuizId(currentQuiz.getId());
            attempt.setUserId(currentUser.getId());
            attempt.setQuestionId(question.getId());
            attempt.setSelectedOption(answered ? submittedAnswer : "NA");
            attempt.setCorrect(isCorrect);
            attempt.setResponseTimeSeconds(responseTime);
            attempt.setAttemptedAt(LocalDateTime.now());
            attempts.add(attempt);

            result.getSubmittedAnswers().put(question.getId(), answered ? submittedAnswer : "Not Answered");
            result.getCorrectnessMap().put(question.getId(), isCorrect);
        }

        quizDAO.saveQuizAttempts(attempts);
        PerformanceAnalysisThread analysisThread = new PerformanceAnalysisThread(currentUser.getId(), performanceDAO);
        analysisThread.start();

        double percentage = currentQuiz.getQuestions().isEmpty()
                ? 0
                : (correctAnswers * 100.0) / currentQuiz.getQuestions().size();

        Map<String, Double> topicAccuracy = new LinkedHashMap<>();
        for (Map.Entry<String, int[]> entry : topicStats.entrySet()) {
            topicAccuracy.put(entry.getKey(), (entry.getValue()[0] * 100.0) / entry.getValue()[1]);
        }

        result.setCorrectAnswers(correctAnswers);
        result.setTotalQuestions(currentQuiz.getQuestions().size());
        result.setPercentageScore(percentage);
        result.setAverageResponseTime(currentQuiz.getQuestions().isEmpty() ? 0 : totalResponseTime / (double) currentQuiz.getQuestions().size());
        result.setIncorrectQuestions(incorrectQuestions);
        result.setTopicAccuracy(topicAccuracy);
        result.setDifficultyRecommendation(buildDifficultyRecommendation(percentage));
        result.setAdaptiveMessage(buildAdaptiveMessage(percentage, incorrectQuestions));
        return result;
    }

    private List<Question> selectByCategory(List<Question> allQuestions,
                                            List<String> topics,
                                            int requiredCount,
                                            int preferredDifficulty,
                                            Set<Integer> usedQuestionIds) {
        if (topics == null || topics.isEmpty()) {
            return new ArrayList<>();
        }

        List<Question> categoryPool = allQuestions.stream()
                .filter(question -> topics.contains(question.getTopic()))
                .filter(question -> !usedQuestionIds.contains(question.getId()))
                .collect(Collectors.toList());

        return takeQuestions(categoryPool, requiredCount, preferredDifficulty, usedQuestionIds);
    }

    private List<Question> fillRemainingQuestions(List<Question> allQuestions,
                                                  int remainingCount,
                                                  int preferredDifficulty,
                                                  Set<Integer> usedQuestionIds) {
        List<Question> remainingPool = allQuestions.stream()
                .filter(question -> !usedQuestionIds.contains(question.getId()))
                .collect(Collectors.toList());
        return takeQuestions(remainingPool, remainingCount, preferredDifficulty, usedQuestionIds);
    }

    private List<Question> takeQuestions(List<Question> questionPool,
                                         int requiredCount,
                                         int preferredDifficulty,
                                         Set<Integer> usedQuestionIds) {
        List<Question> copy = new ArrayList<>(questionPool);
        Collections.shuffle(copy);
        copy.sort(Comparator.comparingInt(question -> Math.abs(question.getDifficultyLevel() - preferredDifficulty)));

        List<Question> selected = new ArrayList<>();
        for (Question question : copy) {
            if (selected.size() >= requiredCount) {
                break;
            }
            if (usedQuestionIds.add(question.getId())) {
                selected.add(question);
            }
        }
        return selected;
    }

    private Question chooseNextQuestion(AdaptiveQuizSession session) {
        int position = session.getQuiz().getQuestions().size();
        List<String> targetTopics = resolveTargetTopics(session, position);
        List<Question> remainingQuestions = session.getCandidateQuestions().stream()
                .filter(question -> !session.getUsedQuestionIds().contains(question.getId()))
                .collect(Collectors.toList());

        List<Question> preferredPool = remainingQuestions.stream()
                .filter(question -> targetTopics.isEmpty() || targetTopics.contains(question.getTopic()))
                .collect(Collectors.toList());

        if (preferredPool.isEmpty()) {
            preferredPool = remainingQuestions;
        }

        if (preferredPool.isEmpty()) {
            return null;
        }

        Collections.shuffle(preferredPool);
        preferredPool.sort(Comparator.comparingInt(question -> Math.abs(question.getDifficultyLevel() - session.getCurrentDifficulty())));
        return preferredPool.get(0);
    }

    private List<String> resolveTargetTopics(AdaptiveQuizSession session, int position) {
        if (session.isRevisionMode()) {
            return new ArrayList<>();
        }

        int weakSlots = Math.max(1, (int) Math.round(session.getMaxQuestions() * AppConstants.WEAK_TOPIC_RATIO));
        int mediumSlots = Math.max(1, (int) Math.round(session.getMaxQuestions() * AppConstants.MEDIUM_TOPIC_RATIO));
        if (position < weakSlots && !session.getWeakTopics().isEmpty()) {
            return session.getWeakTopics();
        }
        if (position < weakSlots + mediumSlots && !session.getMediumTopics().isEmpty()) {
            return session.getMediumTopics();
        }
        if (!session.getStrongTopics().isEmpty()) {
            return session.getStrongTopics();
        }
        if (!session.getMediumTopics().isEmpty()) {
            return session.getMediumTopics();
        }
        return session.getWeakTopics();
    }

    private int determineDifficulty(double averageCompositeScore) {
        if (averageCompositeScore > AppConstants.STRONG_THRESHOLD) {
            return 3;
        }
        if (averageCompositeScore < AppConstants.WEAK_THRESHOLD) {
            return 1;
        }
        return 2;
    }

    private String difficultyLabel(int difficulty) {
        if (difficulty == 3) {
            return "Advanced";
        }
        if (difficulty == 1) {
            return "Foundation";
        }
        return "Intermediate";
    }

    private String buildDifficultyRecommendation(double percentage) {
        if (percentage > 80) {
            return "Performance is above 80%, so the next quiz will move to a higher difficulty.";
        }
        if (percentage < 50) {
            return "Performance is below 50%, so the next quiz will reduce difficulty and focus more on fundamentals.";
        }
        return "Performance is stable, so the next quiz will keep the current difficulty level.";
    }

    private String buildAdaptiveMessage(double percentage, List<Question> incorrectQuestions) {
        if (percentage > 80) {
            return "Excellent work. The adaptive engine detected mastery and will challenge you with harder questions.";
        }
        if (percentage < 50) {
            return "The engine found some weak concepts. Use the revision quiz and explanations to rebuild confidence.";
        }
        if (!incorrectQuestions.isEmpty()) {
            return "Balanced performance. Review the missed explanations to convert medium topics into strengths.";
        }
        return "Consistent performance across topics. Keep practicing to improve speed and accuracy together.";
    }
}
