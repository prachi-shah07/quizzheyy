package com.quizapp.servlet;

import com.quizapp.dao.UserDAO;
import com.quizapp.exception.UserNotFoundException;
import com.quizapp.model.AdaptiveQuizSession;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
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

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = SessionUtil.getLoggedInUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String mode = request.getParameter("mode");
        AdaptiveQuizEngine quizEngine = new AdaptiveQuizEngine();

        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new UserNotFoundException("Logged in user could not be found.");
            }

            HttpSession session = request.getSession();
            TimerThread existingTimer = (TimerThread) session.getAttribute(AppConstants.SESSION_ACTIVE_TIMER);
            if (existingTimer != null) {
                existingTimer.cancelTimer();
            }

            AdaptiveQuizSession adaptiveSession = quizEngine.startAdaptiveQuiz(user, "revision".equalsIgnoreCase(mode));
            session.setAttribute(AppConstants.SESSION_ACTIVE_QUIZ, adaptiveSession.getQuiz());
            session.setAttribute(AppConstants.SESSION_ADAPTIVE_SESSION, adaptiveSession);

            startQuestionTimer(session, adaptiveSession.getQuiz());
            forwardQuizPage(request, response, adaptiveSession);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Unable to generate quiz: " + ex.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }

    static void startQuestionTimer(HttpSession session, Quiz quiz) {
        TimerThread existingTimer = (TimerThread) session.getAttribute(AppConstants.SESSION_ACTIVE_TIMER);
        if (existingTimer != null) {
            existingTimer.cancelTimer();
        }

        TimerThread timerThread = new TimerThread(quiz.getId(), quiz.getPerQuestionTimeLimitSeconds());
        timerThread.start();
        session.setAttribute(AppConstants.SESSION_ACTIVE_TIMER, timerThread);
    }

    static void forwardQuizPage(HttpServletRequest request, HttpServletResponse response, AdaptiveQuizSession adaptiveSession)
            throws ServletException, IOException {
        Question currentQuestion = adaptiveSession.getCurrentQuestion();
        request.setAttribute("quiz", adaptiveSession.getQuiz());
        request.setAttribute("adaptiveSession", adaptiveSession);
        request.setAttribute("currentQuestion", currentQuestion);
        request.setAttribute("questionNumber", adaptiveSession.getCurrentIndex() + 1);
        request.setAttribute("totalQuestionsSoFar", adaptiveSession.getQuiz().getQuestions().size());
        request.setAttribute("maxQuestions", adaptiveSession.getMaxQuestions());
        request.setAttribute("isReviewMode", adaptiveSession.isCurrentQuestionAnswered());
        request.getRequestDispatcher("/quiz.jsp").forward(request, response);
    }
}
