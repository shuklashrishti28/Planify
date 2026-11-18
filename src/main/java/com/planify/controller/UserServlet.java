package com.planify.controller;

import com.planify.dao.UserDAO;
import com.planify.model.User;
import com.planify.util.AccessControl;
import com.planify.util.CsrfProtection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Handles administrative user listing and account changes. */
@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(UserServlet.class.getName());
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    /** Loads user records for the admin management screen. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AccessControl.requireRole(request, response, "ADMIN") == null) {
            return;
        }
        try {
            List<User> userList = userDAO.getAllUsers();
            request.setAttribute("userList", userList);
            request.getRequestDispatcher("manage_users.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to load users", e);
            response.sendRedirect("admin_dashboard.jsp?error=db");
        }
    }

    /** Creates, updates, or deletes a user according to the submitted action. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AccessControl.requireRole(request, response, "ADMIN") == null) {
            return;
        }
        if (!CsrfProtection.isValid(request.getSession(false), request.getParameter("csrfToken"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired form token");
            return;
        }
        String action = request.getParameter("action");
        try {
            if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("userId"));
                String name = request.getParameter("name");
                String email = request.getParameter("email");
                String role = request.getParameter("role");
                if (!isValidName(name) || !isValidEmail(email) || !isValidRole(role)) {
                    response.sendRedirect("users?error=invalid");
                    return;
                }
                boolean success = userDAO.updateUser(id, name.trim(), email.trim(), role);
                response.sendRedirect("users?msg=" + (success ? "updated" : "failed"));
                return;
            }
            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("userId"));
                boolean success = userDAO.deleteUser(id);
                response.sendRedirect("users?msg=" + (success ? "deleted" : "failed"));
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("users?error=invalid");
            return;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to update or delete user", e);
            response.sendRedirect("users?error=failed");
            return;
        }
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        if (!isValidName(name) || !isValidEmail(email) || password == null
                || password.length() < 8 || password.length() > 256 || !isValidRole(role)) {
            response.sendRedirect("users?error=invalid");
            return;
        }
        try {
            boolean success = userDAO.addUser(name.trim(), email.trim(), password, role);
            response.sendRedirect(success ? "users?msg=created" : "users?error=failed");
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Unable to create user; email may already exist", e);
            response.sendRedirect("users?error=duplicate");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isValidName(String name) {
        return hasText(name) && name.trim().length() <= 100;
    }

    private boolean isValidEmail(String email) {
        return hasText(email) && email.trim().length() <= 100
                && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    /** Keeps request values aligned with the role values accepted by the database schema. */
    private boolean isValidRole(String role) {
        return "ADMIN".equals(role) || "PROJECT_MANAGER".equals(role) || "TEAM_MEMBER".equals(role);
    }
}
