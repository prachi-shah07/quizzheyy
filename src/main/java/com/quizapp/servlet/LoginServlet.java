package com.quizapp.servlet;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.util.AppConstants;
import com.quizapp.util.PasswordUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = userDAO.authenticate(email, PasswordUtil.hashPassword(password));
            if (user == null) {
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute(AppConstants.SESSION_USER_ID, user.getId());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Unable to login right now: " + ex.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } finally {
            request.setAttribute("enteredEmail", email);
        }
    }
}
