package com.quizapp.servlet;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.util.PasswordUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            if (userDAO.findByEmail(email) != null) {
                request.setAttribute("errorMessage", "Email already exists. Please use a different email.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            userDAO.createUser(user);

            response.sendRedirect(request.getContextPath() + "/login?registered=true");
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Unable to register right now: " + ex.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
