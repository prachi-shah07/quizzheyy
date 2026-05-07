package com.quizapp.servlet;

import com.quizapp.dao.UserDAO;
import com.quizapp.exception.QuizNotFoundException;
import com.quizapp.exception.UserNotFoundException;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.QuizEvaluationResult;
import com.quizapp.model.User;
import com.quizapp.service.AdaptiveQuizEngine;
import com.quizapp.service.TimerThread;
import com.quizapp.util.AppConstants;
import com.quizapp.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/submit-quiz")
public class SubmitQuizServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = SessionUtil.getLoggedInUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession(false);
        TimerThread timerThread = session == null ? null : (TimerThread) session.getAttribute(AppConstants.SESSION_ACTIVE_TIMER);

        try {
            if (session == null) {
                throw new QuizNotFoundException("Your session has expired. Please start a new quiz.");
            }

            Quiz quiz = (Quiz) session.getAttribute(AppConstants.SESSION_ACTIVE_QUIZ);
            if (quiz == null) {
                throw new QuizNotFoundException("No active quiz found. Please generate a new quiz.");
            }

            User user = userDAO.findById(userId);
            if (user == null) {
                throw new UserNotFoundException("Logged in user could not be found.");
            }

            Map<Integer, String> submittedAnswers = new HashMap<>();
            Map<Integer, Long> responseTimes = new HashMap<>();
            for (Question question : quiz.getQuestions()) {
                String answer = request.getParameter("answer_" + question.getId());
                String timeValue = request.getParameter("time_" + question.getId());
                long responseTime = quiz.getPerQuestionTimeLimitSeconds();
                if (timeValue != null && !timeValue.isBlank()) {
                    responseTime = Long.parseLong(timeValue);
                }
                submittedAnswers.put(question.getId(), answer);
                responseTimes.put(question.getId(), responseTime);
            }

            AdaptiveQuizEngine quizEngine = new AdaptiveQuizEngine();
            quizEngine.prepareEvaluation(quiz, user, submittedAnswers, responseTimes);
            QuizEvaluationResult result = quizEngine.evaluateQuiz();

            if (timerThread != null && timerThread.isExpired()) {
                request.setAttribute("timerMessage", "The quiz timer expired before submission, so unanswered items were treated as incorrect.");
            }

            session.setAttribute(AppConstants.SESSION_LAST_RESULT, result);
            request.setAttribute("quiz", quiz);
            request.setAttribute("result", result);
            request.getRequestDispatcher("/result.jsp").forward(request, response);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Unable to submit quiz: " + ex.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } finally {
            if (timerThread != null) {
                timerThread.cancelTimer();
            }
            if (session != null) {
                session.removeAttribute(AppConstants.SESSION_ACTIVE_QUIZ);
                session.removeAttribute(AppConstants.SESSION_ACTIVE_TIMER);
            }
        }
    }
}
