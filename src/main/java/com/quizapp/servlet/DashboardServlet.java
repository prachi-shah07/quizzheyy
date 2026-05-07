package com.quizapp.servlet;

import com.quizapp.dao.PerformanceDAO;
import com.quizapp.dao.UserDAO;
import com.quizapp.exception.UserNotFoundException;
import com.quizapp.model.Performance;
import com.quizapp.model.QuizEvaluationResult;
import com.quizapp.model.User;
import com.quizapp.util.AppConstants;
import com.quizapp.util.SessionUtil;
import com.quizapp.util.SkillProfileUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final PerformanceDAO performanceDAO = new PerformanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = SessionUtil.getLoggedInUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new UserNotFoundException("Logged in user could not be found.");
            }

            List<Performance> performanceList = performanceDAO.getPerformanceByUserId(userId);
            user.setSkillProfile(SkillProfileUtil.buildSkillProfile(performanceList));

            HttpSession session = request.getSession(false);
            QuizEvaluationResult lastResult = session == null ? null : (QuizEvaluationResult) session.getAttribute(AppConstants.SESSION_LAST_RESULT);

            request.setAttribute("user", user);
            request.setAttribute("performanceList", performanceList);
            request.setAttribute("overallAccuracy", performanceDAO.getOverallAccuracy(userId) * 100);
            request.setAttribute("lastResult", lastResult);
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }
}
