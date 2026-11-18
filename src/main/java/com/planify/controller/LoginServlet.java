package com.planify.controller;
import com.planify.dao.UserDAO;
import com.planify.model.User;
import com.planify.util.DashboardRoutes;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Authenticates users and creates their role-based session. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            response.sendRedirect("login.jsp?error=invalid");
            return;
        }

        try {
            User user = userDAO.authenticateUser(email, password);
            if (user != null) {
                HttpSession previous = request.getSession(false);
                if (previous != null) {
                    previous.invalidate();
                }
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);

                response.sendRedirect(DashboardRoutes.pathFor(user));
            } else {
                response.sendRedirect("login.jsp?error=invalid");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to authenticate user", e);
            response.sendRedirect("login.jsp?error=db");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("login.jsp");
    }

}
