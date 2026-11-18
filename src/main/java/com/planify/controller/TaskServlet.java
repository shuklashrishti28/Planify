package com.planify.controller;

import com.planify.dao.ProjectDAO;
import com.planify.dao.TaskDAO;
import com.planify.dao.UserDAO;
import com.planify.model.Task;
import com.planify.model.User;
import com.planify.util.AccessControl;
import com.planify.util.CsrfProtection;
import com.planify.util.DashboardRoutes;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Handles manager task administration and authorized task status updates. */
@WebServlet("/tasks")
public class TaskServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TaskServlet.class.getName());

    private TaskDAO taskDAO;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        taskDAO = new TaskDAO();
        projectDAO = new ProjectDAO();
        userDAO = new UserDAO();
    }

    /** Loads the task administration view for admins and project managers. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AccessControl.requireRole(request, response, "ADMIN", "PROJECT_MANAGER") == null) {
            return;
        }

        try {
            request.setAttribute("taskList", taskDAO.getAllTasks());
            request.setAttribute("projectList", projectDAO.getAllProjects());
            request.setAttribute("userList", userDAO.getTeamMembers());
            request.setAttribute("taskCounts", taskDAO.getStatusCounts());
            request.getRequestDispatcher("manage_tasks.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to load task administration data", e);
            User currentUser = (User) request.getSession(false).getAttribute("currentUser");
            response.sendRedirect(DashboardRoutes.pathFor(currentUser) + "?error=db");
        }
    }

    /** Routes task form submissions to status update or task creation handlers. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = AccessControl.requireRole(
                request, response, "ADMIN", "PROJECT_MANAGER", "TEAM_MEMBER");
        if (user == null) {
            return;
        }
        if (!CsrfProtection.isValid(request.getSession(false), request.getParameter("csrfToken"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired form token");
            return;
        }

        try {
            String action = request.getParameter("action");
            if ("updateStatus".equals(action)) {
                updateTaskStatus(request, response, user);
            } else if (!isManager(user)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } else if ("create".equals(action) || action == null) {
                saveTask(request, response, false);
            } else if ("update".equals(action)) {
                saveTask(request, response, true);
            } else if ("delete".equals(action)) {
                deleteTask(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown task action");
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect("tasks?error=invalid");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to save task changes", e);
            response.sendRedirect("tasks?error=failed");
        }
    }

    private void updateTaskStatus(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {
        int taskId = Integer.parseInt(request.getParameter("taskId"));
        String status = request.getParameter("status");
        if (!isValidStatus(status)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid task status");
            return;
        }

        boolean updated = isManager(user)
                ? taskDAO.updateTaskStatus(taskId, status)
                : taskDAO.updateTaskStatus(taskId, user.getUserId(), status);
        if (!updated) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found or not assigned to this user");
            return;
        }

        response.sendRedirect(isManager(user) ? "tasks?msg=updated" : "member");
    }

    private void saveTask(HttpServletRequest request, HttpServletResponse response, boolean update)
            throws SQLException, IOException {
        int projectId = Integer.parseInt(request.getParameter("projectId"));
        int assignedTo = Integer.parseInt(request.getParameter("assignedTo"));
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String status = request.getParameter("status");
        String dueDateValue = request.getParameter("dueDate");

        if (!hasText(title) || title.trim().length() > 150 || !hasText(description)
                || !isValidStatus(status) || dueDateValue == null) {
            throw new IllegalArgumentException("Invalid task form values");
        }
        if (!projectDAO.exists(projectId) || !userDAO.isTeamMember(assignedTo)) {
            throw new IllegalArgumentException("Tasks must reference an existing project and team member");
        }

        int taskId = update ? Integer.parseInt(request.getParameter("taskId")) : 0;
        Task task = new Task(taskId, projectId, assignedTo, title.trim(), description.trim(), status,
                Date.valueOf(dueDateValue));
        boolean saved = update ? taskDAO.updateTask(task) : taskDAO.createTask(task);
        String message = update ? "updated" : "created";
        response.sendRedirect(saved ? "tasks?msg=" + message : "tasks?error=failed");
    }

    private void deleteTask(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int taskId = Integer.parseInt(request.getParameter("taskId"));
        boolean deleted = taskDAO.deleteTask(taskId);
        response.sendRedirect(deleted ? "tasks?msg=deleted" : "tasks?error=failed");
    }

    private boolean isManager(User user) {
        return "ADMIN".equals(user.getRole()) || "PROJECT_MANAGER".equals(user.getRole());
    }

    private boolean isValidStatus(String status) {
        return "PENDING".equals(status) || "IN_PROGRESS".equals(status) || "COMPLETED".equals(status);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
