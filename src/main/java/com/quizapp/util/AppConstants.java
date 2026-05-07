package com.quizapp.util;

public final class AppConstants {

    public static final int DEFAULT_QUIZ_SIZE = 10;
    public static final int PER_QUESTION_TIME_LIMIT_SECONDS = 60;
    public static final int REVISION_QUIZ_SIZE = 5;

    public static final double WEAK_TOPIC_RATIO = 0.5;
    public static final double MEDIUM_TOPIC_RATIO = 0.3;
    public static final double STRONG_TOPIC_RATIO = 0.2;

    public static final double STRONG_THRESHOLD = 0.80;
    public static final double WEAK_THRESHOLD = 0.50;

    public static final String SESSION_USER_ID = "loggedInUserId";
    public static final String SESSION_ACTIVE_QUIZ = "activeQuiz";
    public static final String SESSION_ACTIVE_TIMER = "activeTimer";
    public static final String SESSION_ADAPTIVE_SESSION = "adaptiveQuizSession";
    public static final String SESSION_LAST_RESULT = "lastQuizResult";

    private AppConstants() {
    }
}
