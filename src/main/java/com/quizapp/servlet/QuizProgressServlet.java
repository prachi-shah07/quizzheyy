package com.quizapp.servlet;

import com.quizapp.dao.UserDAO;
import com.quizapp.exception.QuizNotFoundException;
import com.quizapp.exception.UserNotFoundException;
import com.quizapp.model.AdaptiveQuizSession;
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

@WebServlet("/quiz-progress")
public class QuizProgressServlet extends HttpServlet {

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
        AdaptiveQuizEngine quizEngine = new AdaptiveQuizEngine();

        try {
            if (session == null) {
                throw new QuizNotFoundException("Your session expired. Please start a new quiz.");
            }

            AdaptiveQuizSession adaptiveSession = (AdaptiveQuizSession) session.getAttribute(AppConstants.SESSION_ADAPTIVE_SESSION);
            if (adaptiveSession == null || adaptiveSession.getCurrentQuestion() == null) {
                throw new QuizNotFoundException("No adaptive quiz session was found. Please start a new quiz.");
            }

            User user = userDAO.findById(userId);
            if (user == null) {
                throw new UserNotFoundException("Logged in user could not be found.");
            }

            adaptiveSession.setUser(user);
            String action = request.getParameter("action");
            if (action == null || action.isBlank()) {
                action = "next";
            }

            if ("previous".equalsIgnoreCase(action)) {
                if (timerThread != null) {
                    timerThread.cancelTimer();
                    session.removeAttribute(AppConstants.SESSION_ACTIVE_TIMER);
                }
                quizEngine.movePrevious(adaptiveSession);
                if (!adaptiveSession.isCurrentQuestionAnswered()) {
                    QuizServlet.startQuestionTimer(session, adaptiveSession.getQuiz());
                }
                QuizServlet.forwardQuizPage(request, response, adaptiveSession);
                return;
            }

            if ("next".equalsIgnoreCase(action)) {
                if (adaptiveSession.isCurrentQuestionAnswered()) {
                    if (timerThread != null) {
                        timerThread.cancelTimer();
                        session.removeAttribute(AppConstants.SESSION_ACTIVE_TIMER);
                    }
                    quizEngine.moveNext(adaptiveSession);
                } else {
                    String answer = request.getParameter("answer");
                    long elapsed = parseElapsed(request.getParameter("elapsedSeconds"), adaptiveSession.getQuiz().getPerQuestionTimeLimitSeconds());
                    if (timerThread != null && timerThread.isExpired()) {
                        answer = null;
                        elapsed = adaptiveSession.getQuiz().getPerQuestionTimeLimitSeconds();
                    }

                    quizEngine.recordCurrentAnswer(adaptiveSession, answer, elapsed);
                    boolean hasNext = quizEngine.appendNextQuestion(adaptiveSession);
                    if (!hasNext) {
                        completeQuiz(request, response, session, quizEngine, adaptiveSession);
                        return;
                    }
                }

                if (!adaptiveSession.isCurrentQuestionAnswered()) {
                    QuizServlet.startQuestionTimer(session, adaptiveSession.getQuiz());
                }
                QuizServlet.forwardQuizPage(request, response, adaptiveSession);
                return;
            }

            if ("submit".equalsIgnoreCase(action)) {
                if (!adaptiveSession.isCurrentQuestionAnswered()) {
                    String answer = request.getParameter("answer");
                    long elapsed = parseElapsed(request.getParameter("elapsedSeconds"), adaptiveSession.getQuiz().getPerQuestionTimeLimitSeconds());
                    if (timerThread != null && timerThread.isExpired()) {
                        answer = null;
                        elapsed = adaptiveSession.getQuiz().getPerQuestionTimeLimitSeconds();
                    }
                    quizEngine.recordCurrentAnswer(adaptiveSession, answer, elapsed);
                }
                completeQuiz(request, response, session, quizEngine, adaptiveSession);
                return;
            }

            QuizServlet.forwardQuizPage(request, response, adaptiveSession);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Unable to continue quiz: " + ex.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }

    private void completeQuiz(HttpServletRequest request,
                              HttpServletResponse response,
                              HttpSession session,
                              AdaptiveQuizEngine quizEngine,
                              AdaptiveQuizSession adaptiveSession) throws Exception {
        TimerThread timerThread = (TimerThread) session.getAttribute(AppConstants.SESSION_ACTIVE_TIMER);
        if (timerThread != null) {
            timerThread.cancelTimer();
        }

        quizEngine.prepareEvaluation(
                adaptiveSession.getQuiz(),
                adaptiveSession.getUser(),
                adaptiveSession.getSubmittedAnswers(),
                adaptiveSession.getResponseTimes());
        QuizEvaluationResult result = quizEngine.evaluateQuiz();

        session.setAttribute(AppConstants.SESSION_LAST_RESULT, result);
        session.removeAttribute(AppConstants.SESSION_ADAPTIVE_SESSION);
        session.removeAttribute(AppConstants.SESSION_ACTIVE_TIMER);
        session.removeAttribute(AppConstants.SESSION_ACTIVE_QUIZ);

        request.setAttribute("quiz", adaptiveSession.getQuiz());
        request.setAttribute("result", result);
        request.getRequestDispatcher("/result.jsp").forward(request, response);
    }

    private long parseElapsed(String rawElapsed, int defaultValue) {
        if (rawElapsed == null || rawElapsed.isBlank()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(rawElapsed);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
