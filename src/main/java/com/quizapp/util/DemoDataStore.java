package com.quizapp.util;

import com.quizapp.model.Performance;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.QuizAttempt;
import com.quizapp.model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class DemoDataStore {

    private static final Path APP_DATA_DIRECTORY = Paths.get(System.getProperty("user.dir"), "app-data");
    private static final Path USERS_FILE = APP_DATA_DIRECTORY.resolve("demo-users.txt");
    private static final Path ATTEMPTS_FILE = APP_DATA_DIRECTORY.resolve("demo-attempts.txt");
    private static final AtomicInteger USER_ID_SEQUENCE = new AtomicInteger(3);
    private static final AtomicInteger QUIZ_ID_SEQUENCE = new AtomicInteger(3);
    private static final Map<Integer, User> USERS = new ConcurrentHashMap<>();
    private static final Map<String, Integer> EMAIL_TO_USER_ID = new ConcurrentHashMap<>();
    private static final Map<Integer, Question> QUESTIONS = new LinkedHashMap<>();
    private static final List<QuizAttempt> QUIZ_ATTEMPTS = new ArrayList<>();
    private static final Map<Integer, Quiz> QUIZZES = new ConcurrentHashMap<>();

    static {
        seedUsers();
        seedQuestions();
        seedAttempts();
        loadPersistedUsers();
        loadPersistedAttempts();
    }

    private DemoDataStore() {
    }

    private static void seedUsers() {
        addSeedUser(1, "Admin User", "admin@quizapp.com", PasswordUtil.hashPassword("Admin@123"));
        addSeedUser(2, "Student Demo", "student@quizapp.com", PasswordUtil.hashPassword("Student@123"));
    }

    private static void addSeedUser(int id, String fullName, String email, String passwordHash) {
        User user = new User();
        user.setId(id);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setCreatedAt(LocalDateTime.now().minusDays(7));
        USERS.put(id, user);
        EMAIL_TO_USER_ID.put(email.toLowerCase(), id);
    }

    private static void seedQuestions() {
        addQuestion(1, "Java Basics", 1, "Which keyword is used to inherit a class in Java?", "this", "extends", "implements", "super", "B", "The extends keyword is used for class inheritance in Java.");
        addQuestion(2, "Java Basics", 2, "Which data type is best suited for storing true or false values?", "int", "String", "boolean", "char", "C", "boolean stores only true or false values.");
        addQuestion(3, "Java Basics", 3, "What is the output type of 5 / 2 in Java when both operands are integers?", "2.5", "2", "3", "Compilation error", "B", "Integer division truncates the decimal part, so 5 / 2 becomes 2.");
        addQuestion(4, "OOP", 1, "Which OOP principle hides internal data using private fields and public methods?", "Polymorphism", "Abstraction", "Encapsulation", "Inheritance", "C", "Encapsulation protects data and exposes behavior through methods.");
        addQuestion(5, "OOP", 2, "What is the main purpose of an abstract class?", "To store only constants", "To prevent object creation of incomplete designs", "To replace interfaces completely", "To avoid inheritance", "B", "Abstract classes provide partial implementation and cannot be instantiated directly.");
        addQuestion(6, "OOP", 3, "Which statement about method overriding is correct?", "It requires static methods", "It happens when a subclass provides its own version of a parent method", "It is only possible with private methods", "It removes inheritance", "B", "Overriding lets a subclass redefine inherited behavior with the same signature.");
        addQuestion(7, "Operating System", 1, "Which operating system component decides which process gets the CPU next?", "File manager", "Scheduler", "Loader", "Device driver", "B", "The scheduler selects the next process based on the scheduling algorithm.");
        addQuestion(8, "Operating System", 2, "What is the main purpose of virtual memory?", "To replace the CPU", "To increase monitor resolution", "To give processes the illusion of larger memory space", "To compress files automatically", "C", "Virtual memory uses disk space to extend apparent main memory.");
        addQuestion(9, "Operating System", 3, "Which problem occurs when two or more processes wait forever for resources held by each other?", "Thrashing", "Fragmentation", "Deadlock", "Starvation", "C", "Deadlock happens when circular waiting prevents progress.");
        addQuestion(10, "Computer Networks", 1, "Which device forwards packets between different networks?", "Hub", "Switch", "Router", "Repeater", "C", "A router connects networks and forwards packets using IP addresses.");
        addQuestion(11, "Computer Networks", 2, "What does HTTP primarily define?", "How operating systems boot", "Rules for transferring web resources", "How databases normalize tables", "How processors schedule tasks", "B", "HTTP is the protocol used to transfer web pages and related resources.");
        addQuestion(12, "Computer Networks", 3, "Which layer of the OSI model is responsible for end-to-end communication and reliability?", "Physical", "Transport", "Data Link", "Network", "B", "The transport layer handles end-to-end delivery, including reliability in protocols like TCP.");
        addQuestion(13, "Exception Handling", 1, "Which block always executes whether an exception occurs or not?", "catch", "finally", "throw", "try", "B", "finally is designed for cleanup work that must always run.");
        addQuestion(14, "Exception Handling", 2, "Which keyword is used to create a custom exception object?", "catch", "throws", "throw", "new", "D", "A custom exception object is instantiated using new before being thrown.");
        addQuestion(15, "Exception Handling", 3, "Which is true about checked exceptions?", "They are ignored by the compiler", "They must be handled or declared", "They only happen in threads", "They can only be runtime exceptions", "B", "Checked exceptions require explicit handling or declaration.");
        addQuestion(16, "Data Structures", 1, "Which data structure follows the Last In First Out principle?", "Queue", "Stack", "Linked List", "Tree", "B", "A stack removes the most recently inserted item first.");
        addQuestion(17, "Data Structures", 2, "What is the average-case time complexity of binary search on a sorted array?", "O(n)", "O(log n)", "O(n log n)", "O(1)", "B", "Binary search halves the search space on each step, giving logarithmic complexity.");
        addQuestion(18, "Data Structures", 3, "Which traversal visits the root node between the left and right subtrees in a binary tree?", "Preorder", "Postorder", "Inorder", "Level order", "C", "Inorder traversal processes left subtree, root, then right subtree.");
        addQuestion(19, "Web Development", 1, "Which language is primarily used to structure content on web pages?", "CSS", "JavaScript", "HTML", "SQL", "C", "HTML defines the structure and content of a webpage.");
        addQuestion(20, "Web Development", 2, "What is the main role of CSS in web development?", "To store records", "To style and layout web pages", "To compile Java code", "To manage network routing", "B", "CSS controls the presentation, layout, and design of HTML content.");
        addQuestion(21, "Web Development", 3, "Which HTTP method is commonly used to submit form data that creates a new resource?", "GET", "POST", "DELETE", "TRACE", "B", "POST is typically used when submitting form data that creates or changes server-side data.");
    }

    private static void addQuestion(int id, String topic, int difficulty, String text,
                                    String optionA, String optionB, String optionC, String optionD,
                                    String correctOption, String explanation) {
        Question question = new Question();
        question.setId(id);
        question.setTopic(topic);
        question.setDifficultyLevel(difficulty);
        question.setQuestionText(text);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrectOption(correctOption);
        question.setExplanation(explanation);
        QUESTIONS.put(id, question);
    }

    private static void seedAttempts() {
        addSeedAttempt(1, 2, 1, "B", true, 11);
        addSeedAttempt(1, 2, 4, "C", true, 12);
        addSeedAttempt(1, 2, 7, "A", false, 22);
        addSeedAttempt(1, 2, 10, "B", true, 18);
        addSeedAttempt(1, 2, 16, "A", false, 18);
        addSeedAttempt(1, 2, 19, "C", true, 13);
        addSeedAttempt(1, 2, 13, "B", true, 14);
        addSeedAttempt(2, 2, 2, "A", false, 24);
        addSeedAttempt(2, 2, 5, "B", true, 19);
        addSeedAttempt(2, 2, 8, "B", true, 16);
        addSeedAttempt(2, 2, 11, "C", true, 17);
        addSeedAttempt(2, 2, 17, "B", true, 15);
        addSeedAttempt(2, 2, 20, "A", false, 21);
        addSeedAttempt(2, 2, 14, "D", true, 20);
    }

    private static void addSeedAttempt(int quizId, int userId, int questionId, String selectedOption, boolean correct, long responseTimeSeconds) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuizId(quizId);
        attempt.setUserId(userId);
        attempt.setQuestionId(questionId);
        attempt.setSelectedOption(selectedOption);
        attempt.setCorrect(correct);
        attempt.setResponseTimeSeconds(responseTimeSeconds);
        attempt.setAttemptedAt(LocalDateTime.now().minusDays(2));
        QUIZ_ATTEMPTS.add(attempt);
    }

    public static synchronized User createUser(User user) {
        User copy = copyUser(user);
        copy.setId(USER_ID_SEQUENCE.getAndIncrement());
        copy.setCreatedAt(LocalDateTime.now());
        USERS.put(copy.getId(), copy);
        EMAIL_TO_USER_ID.put(copy.getEmail().toLowerCase(), copy.getId());
        saveUsers();
        return copyUser(copy);
    }

    public static User findUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        Integer userId = EMAIL_TO_USER_ID.get(email.toLowerCase());
        return userId == null ? null : copyUser(USERS.get(userId));
    }

    public static User findUserById(int userId) {
        return copyUser(USERS.get(userId));
    }

    public static User authenticate(String email, String passwordHash) {
        User user = findUserByEmail(email);
        if (user != null && user.getPasswordHash().equals(passwordHash)) {
            return user;
        }
        return null;
    }

    public static List<Question> getAllQuestions() {
        return QUESTIONS.values().stream().map(DemoDataStore::copyQuestion).collect(Collectors.toList());
    }

    public static List<Question> getIncorrectQuestionsForUser(int userId, int limit) {
        Set<Integer> questionIds = QUIZ_ATTEMPTS.stream()
                .filter(item -> item.getUserId() == userId && !item.isCorrect())
                .sorted(Comparator.comparing(QuizAttempt::getAttemptedAt).reversed())
                .map(QuizAttempt::getQuestionId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Question> questions = new ArrayList<>();
        for (Integer questionId : questionIds) {
            if (questions.size() >= limit) {
                break;
            }
            Question question = QUESTIONS.get(questionId);
            if (question != null) {
                questions.add(copyQuestion(question));
            }
        }
        return questions;
    }

    public static synchronized int createQuiz(Quiz quiz) {
        Quiz copy = copyQuiz(quiz);
        int id = QUIZ_ID_SEQUENCE.getAndIncrement();
        copy.setId(id);
        QUIZZES.put(id, copy);
        return id;
    }

    public static synchronized void saveQuizAttempts(List<QuizAttempt> attempts) {
        for (QuizAttempt attempt : attempts) {
            QUIZ_ATTEMPTS.add(copyAttempt(attempt));
        }
        saveAttempts();
    }

    public static List<Performance> getPerformanceByUserId(int userId) {
        Map<String, List<QuizAttempt>> attemptsByTopic = getAttemptsByTopic(userId);
        List<Performance> performanceList = new ArrayList<>();
        for (Map.Entry<String, List<QuizAttempt>> entry : attemptsByTopic.entrySet()) {
            performanceList.add(buildPerformance(userId, entry.getKey(), entry.getValue()));
        }
        performanceList.sort(Comparator.comparingDouble(Performance::getCompositeScore));
        return performanceList;
    }

    public static double getOverallAccuracy(int userId) {
        List<Performance> performanceList = getPerformanceByUserId(userId);
        if (performanceList.isEmpty()) {
            return 0.0;
        }
        return performanceList.stream().mapToDouble(Performance::getAccuracy).average().orElse(0.0);
    }

    public static void refreshPerformanceForUser(int userId) {
        getPerformanceByUserId(userId);
    }

    private static Map<String, List<QuizAttempt>> getAttemptsByTopic(int userId) {
        Map<String, List<QuizAttempt>> attemptsByTopic = new LinkedHashMap<>();
        for (QuizAttempt attempt : QUIZ_ATTEMPTS) {
            if (attempt.getUserId() != userId) {
                continue;
            }
            Question question = QUESTIONS.get(attempt.getQuestionId());
            if (question == null) {
                continue;
            }
            attemptsByTopic.computeIfAbsent(question.getTopic(), key -> new ArrayList<>()).add(copyAttempt(attempt));
        }
        return attemptsByTopic;
    }

    private static Performance buildPerformance(int userId, String topic, List<QuizAttempt> attempts) {
        int totalAttempts = attempts.size();
        double accuracy = attempts.stream().filter(QuizAttempt::isCorrect).count() / (double) totalAttempts;
        double averageResponse = attempts.stream().mapToLong(QuizAttempt::getResponseTimeSeconds).average().orElse(0);
        double speed = averageResponse >= 30 ? 0 : Math.max(0, 1 - (averageResponse / 30.0));
        double meanCorrect = accuracy;
        double variance = 0;
        for (QuizAttempt attempt : attempts) {
            double value = attempt.isCorrect() ? 1 : 0;
            variance += Math.pow(value - meanCorrect, 2);
        }
        variance = totalAttempts <= 1 ? 0 : variance / totalAttempts;
        double consistency = Math.max(0, 1 - Math.sqrt(variance));
        double composite = (accuracy * 0.6) + (speed * 0.2) + (consistency * 0.2);

        Performance performance = new Performance();
        performance.setUserId(userId);
        performance.setTopic(topic);
        performance.setAccuracy(accuracy);
        performance.setSpeed(speed);
        performance.setConsistency(consistency);
        performance.setCompositeScore(composite);
        performance.setAverageResponseTime(averageResponse);
        performance.setAttempts(totalAttempts);
        performance.setLastUpdated(LocalDateTime.now());
        return performance;
    }

    private static User copyUser(User source) {
        if (source == null) {
            return null;
        }
        User user = new User();
        user.setId(source.getId());
        user.setFullName(source.getFullName());
        user.setEmail(source.getEmail());
        user.setPasswordHash(source.getPasswordHash());
        user.setCreatedAt(source.getCreatedAt());
        user.setSkillProfile(source.getSkillProfile());
        return user;
    }

    private static Question copyQuestion(Question source) {
        Question question = new Question();
        question.setId(source.getId());
        question.setTopic(source.getTopic());
        question.setDifficultyLevel(source.getDifficultyLevel());
        question.setQuestionText(source.getQuestionText());
        question.setOptionA(source.getOptionA());
        question.setOptionB(source.getOptionB());
        question.setOptionC(source.getOptionC());
        question.setOptionD(source.getOptionD());
        question.setCorrectOption(source.getCorrectOption());
        question.setExplanation(source.getExplanation());
        return question;
    }

    private static Quiz copyQuiz(Quiz source) {
        Quiz quiz = new Quiz();
        quiz.setId(source.getId());
        quiz.setUserId(source.getUserId());
        quiz.setQuestions(source.getQuestions().stream().map(DemoDataStore::copyQuestion).collect(Collectors.toList()));
        quiz.setGeneratedAt(source.getGeneratedAt());
        quiz.setRevisionQuiz(source.isRevisionQuiz());
        quiz.setPerQuestionTimeLimitSeconds(source.getPerQuestionTimeLimitSeconds());
        quiz.setTargetedDifficulty(source.getTargetedDifficulty());
        return quiz;
    }

    private static QuizAttempt copyAttempt(QuizAttempt source) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(source.getId());
        attempt.setQuizId(source.getQuizId());
        attempt.setUserId(source.getUserId());
        attempt.setQuestionId(source.getQuestionId());
        attempt.setSelectedOption(source.getSelectedOption());
        attempt.setCorrect(source.isCorrect());
        attempt.setResponseTimeSeconds(source.getResponseTimeSeconds());
        attempt.setAttemptedAt(source.getAttemptedAt());
        return attempt;
    }

    private static void loadPersistedUsers() {
        if (!Files.exists(USERS_FILE)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8);
            int maxUserId = USER_ID_SEQUENCE.get() - 1;
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\t", -1);
                if (parts.length < 5) {
                    continue;
                }

                User user = new User();
                user.setId(Integer.parseInt(parts[0]));
                user.setFullName(unescape(parts[1]));
                user.setEmail(unescape(parts[2]));
                user.setPasswordHash(parts[3]);
                user.setCreatedAt(LocalDateTime.parse(parts[4]));

                USERS.put(user.getId(), user);
                EMAIL_TO_USER_ID.put(user.getEmail().toLowerCase(), user.getId());
                maxUserId = Math.max(maxUserId, user.getId());
            }
            USER_ID_SEQUENCE.set(maxUserId + 1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void loadPersistedAttempts() {
        if (!Files.exists(ATTEMPTS_FILE)) {
            return;
        }

        try {
            QUIZ_ATTEMPTS.clear();
            List<String> lines = Files.readAllLines(ATTEMPTS_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\t", -1);
                if (parts.length < 7) {
                    continue;
                }

                QuizAttempt attempt = new QuizAttempt();
                attempt.setQuizId(Integer.parseInt(parts[0]));
                attempt.setUserId(Integer.parseInt(parts[1]));
                attempt.setQuestionId(Integer.parseInt(parts[2]));
                attempt.setSelectedOption(unescape(parts[3]));
                attempt.setCorrect(Boolean.parseBoolean(parts[4]));
                attempt.setResponseTimeSeconds(Long.parseLong(parts[5]));
                attempt.setAttemptedAt(LocalDateTime.parse(parts[6]));
                QUIZ_ATTEMPTS.add(attempt);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static synchronized void saveUsers() {
        List<String> lines = USERS.values().stream()
                .sorted(Comparator.comparingInt(User::getId))
                .map(user -> user.getId()
                        + "\t" + escape(user.getFullName())
                        + "\t" + escape(user.getEmail())
                        + "\t" + user.getPasswordHash()
                        + "\t" + user.getCreatedAt())
                .collect(Collectors.toList());
        writeLines(USERS_FILE, lines);
    }

    private static synchronized void saveAttempts() {
        List<String> lines = QUIZ_ATTEMPTS.stream()
                .map(attempt -> attempt.getQuizId()
                        + "\t" + attempt.getUserId()
                        + "\t" + attempt.getQuestionId()
                        + "\t" + escape(attempt.getSelectedOption())
                        + "\t" + attempt.isCorrect()
                        + "\t" + attempt.getResponseTimeSeconds()
                        + "\t" + attempt.getAttemptedAt())
                .collect(Collectors.toList());
        writeLines(ATTEMPTS_FILE, lines);
    }

    private static void writeLines(Path file, List<String> lines) {
        try {
            Files.createDirectories(APP_DATA_DIRECTORY);
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private static String unescape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (escaped) {
                if (current == 't') {
                    builder.append('\t');
                } else if (current == 'n') {
                    builder.append('\n');
                } else {
                    builder.append(current);
                }
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else {
                builder.append(current);
            }
        }
        if (escaped) {
            builder.append('\\');
        }
        return builder.toString();
    }
}
