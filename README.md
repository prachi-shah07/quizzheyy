# AI Personalized Quiz System

Java-based adaptive quiz web application built with Core Java, Servlets, JSP, JDBC, and MySQL.

## Features

- Adaptive quiz generation using weak, medium, and strong topics in a 50/30/20 ratio
- OOP concepts with abstraction, inheritance, encapsulation, and interface implementation
- Background multithreading with `TimerThread` and `PerformanceAnalysisThread`
- Custom exception handling using `UserNotFoundException`, `QuizNotFoundException`, and `InvalidAnswerException`
- JDBC DAO layer using `PreparedStatement`
- Revision quizzes for incorrect questions
- Result explanations and dashboard analytics
- Recommendation engine based on topic performance

## Project Structure

```text
src/main/java/com/quizapp/
  model/
  dao/
  service/
  servlet/
  util/
  exception/
src/main/webapp/
  *.jsp
  css/
database/
  schema.sql
  sample_data.sql
```

## Prerequisites

- JDK 11 or later
- Maven 3.8+
- MySQL 8+
- Apache Tomcat 9+

## Database Setup

1. Create the schema and tables:

   ```sql
   SOURCE database/schema.sql;
   ```

2. Load demo records:

   ```sql
   SOURCE database/sample_data.sql;
   ```

3. Update database credentials in [db.properties](C:\Users\inder\OneDrive\Documents\New project\src\main\resources\db.properties) if your MySQL username or password is different.

## Default Demo Credentials

- `admin@quizapp.com` / `Admin@123`
- `student@quizapp.com` / `Student@123`

## Build and Run on Tomcat

1. From the project root, build the WAR:

   ```bash
   mvn clean package
   ```

2. Copy the generated WAR from `target/ai-personalized-quiz-system.war` into your Tomcat `webapps` folder.

3. Start Tomcat.

4. Open:

   ```text
   http://localhost:8080/ai-personalized-quiz-system/
   ```

## Notes

- The quiz engine raises difficulty when score is above 80% and lowers it when score is below 50%.
- The dashboard reads from the `performance` table, which is refreshed by `PerformanceAnalysisThread` after each submission.
- `TimerThread` manages quiz timing in the background while the JSP page also shows a visible countdown for the learner.
