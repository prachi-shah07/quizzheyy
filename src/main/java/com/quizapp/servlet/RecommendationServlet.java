package com.quizapp.servlet;

import com.quizapp.dao.UserDAO;
import com.quizapp.exception.UserNotFoundException;
import com.quizapp.model.User;
import com.quizapp.service.AdaptiveRecommendationService;
import com.quizapp.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/recommendations")
public class RecommendationServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AdaptiveRecommendationService recommendationService = new AdaptiveRecommendationService();

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

            request.setAttribute("recommendations", recommendationService.getRecommendations(user));
            request.getRequestDispatcher("/recommendation.jsp").forward(request, response);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Unable to load recommendations: " + ex.getMessage());
            request.getRequestDispatcher("/recommendation.jsp").forward(request, response);
        }
    }
}
