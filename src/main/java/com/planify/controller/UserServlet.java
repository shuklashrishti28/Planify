package com.planify.controller;

import com.planify.dao.UserDAO;
import com.planify.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    // Users display karne ke liye
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<User> userList = userDAO.getAllUsers();
            request.setAttribute("userList", userList);
            request.getRequestDispatcher("manage_users.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("admin_dashboard.jsp?error=db");
        }
    }

    // Naya user create karne ke liye
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        try {
            boolean success = userDAO.addUser(name, email, password, role);
            if (success) {
                response.sendRedirect("users?msg=created");
            } else {
                response.sendRedirect("users?error=failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("users?error=duplicate");
        }
    }
}