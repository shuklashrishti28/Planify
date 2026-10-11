package com.planify.controller;
import com.planify.dao.UserDAO;
import com.planify.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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

        try {
            User user = userDAO.authenticateUser(email, password);
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);

                if ("ADMIN".equals(user.getRole())) {
                    response.sendRedirect("admin_dashboard.jsp");
                } else if ("PROJECT_MANAGER".equals(user.getRole())) {
                    response.sendRedirect("pm_dashboard.jsp");
                } else {
                    response.sendRedirect("member_dashboard.jsp");
                }
            } else {
                response.sendRedirect("login.jsp?error=invalid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=db");
        }
    }
}
