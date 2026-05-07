CREATE DATABASE IF NOT EXISTS ai_quiz_system;
USE ai_quiz_system;

DROP TABLE IF EXISTS performance;
DROP TABLE IF EXISTS quiz_attempts;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    topic VARCHAR(80) NOT NULL,
    difficulty_level INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_option CHAR(1) NOT NULL,
    explanation TEXT NOT NULL
);

CREATE TABLE quizzes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    is_revision BOOLEAN NOT NULL DEFAULT FALSE,
    targeted_difficulty VARCHAR(40) NOT NULL,
    generated_at DATETIME NOT NULL,
    CONSTRAINT fk_quizzes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE quiz_attempts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    quiz_id INT NOT NULL,
    user_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option VARCHAR(10) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    response_time_seconds BIGINT NOT NULL,
    attempted_at DATETIME NOT NULL,
    CONSTRAINT fk_attempt_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    CONSTRAINT fk_attempt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_attempt_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE TABLE performance (
    user_id INT NOT NULL,
    topic VARCHAR(80) NOT NULL,
    accuracy DOUBLE NOT NULL,
    speed DOUBLE NOT NULL,
    consistency DOUBLE NOT NULL,
    composite_score DOUBLE NOT NULL,
    average_response_time DOUBLE NOT NULL,
    attempts INT NOT NULL,
    last_updated DATETIME NOT NULL,
    PRIMARY KEY (user_id, topic),
    CONSTRAINT fk_performance_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
