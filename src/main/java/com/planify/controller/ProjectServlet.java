package com.planify.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.planify.dao.ProjectDAO;
import com.planify.model.Project;
import com.planify.model.User;
import com.planify.util.AccessControl;
import com.planify.util.CsrfProtection;
import com.planify.util.DashboardRoutes;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Handles project listing and manager/admin project changes. */
@WebServlet("/projects")
public class ProjectServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProjectServlet.class.getName());
    private ProjectDAO projectDAO;

    @Override
    public void init() {
        projectDAO = new ProjectDAO();
    }

    /** Restricts project data access to administrators and project managers. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AccessControl.requireRole(request, response, "ADMIN", "PROJECT_MANAGER") == null) {
            return;
        }
        try {
            List<Project> projectList = projectDAO.getAllProjects();
            request.setAttribute("projectList", projectList);
            request.getRequestDispatcher("manage_projects.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to load projects", e);
            User user = (User) request.getSession(false).getAttribute("currentUser");
            response.sendRedirect(DashboardRoutes.pathFor(user) + "?error=db");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = AccessControl.requireRole(request, response, "ADMIN", "PROJECT_MANAGER");
        if (currentUser == null) {
            return;
        }
        if (!CsrfProtection.isValid(request.getSession(false), request.getParameter("csrfToken"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or expired form token");
            return;
        }

        String action = request.getParameter("action");
        try {
            switch (action == null ? "create" : action) {
                case "delete" ->
                    deleteProject(request, response);
                case "update" ->
                    updateProject(request, response, currentUser);
                case "create" ->
                    createProject(request, response, currentUser);
                default ->
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown project action");
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect("projects?error=invalid");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to save project changes", e);
            response.sendRedirect("projects?error=failed");
        }
    }

    /** Creates a project from the validated form values. */
    private void createProject(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {
        Project project = readProjectForm(request, user.getUserId());
        boolean created = projectDAO.createProject(project);
        response.sendRedirect(created ? "projects?msg=created" : "projects?error=failed");
    }

    /** Updates an existing project's title, description, and timeline. */
    private void updateProject(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException, IOException {
        int projectId = Integer.parseInt(request.getParameter("projectId"));
        Project formProject = readProjectForm(request, user.getUserId());
        Project project = new Project(projectId, formProject.getTitle(), formProject.getDescription(),
                formProject.getStartDate(), formProject.getEndDate(), user.getUserId());
        boolean updated = projectDAO.updateProject(project);
        response.sendRedirect(updated ? "projects?msg=updated" : "projects?error=failed");
    }

    /** Deletes a project and relies on the database cascade for its tasks. */
    private void deleteProject(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int projectId = Integer.parseInt(request.getParameter("projectId"));
        boolean deleted = projectDAO.deleteProject(projectId);
        response.sendRedirect(deleted ? "projects?msg=deleted" : "projects?error=failed");
    }

    /** Parses and validates fields shared by project creation and editing. */
    private Project readProjectForm(HttpServletRequest request, int createdBy) {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String startDateValue = request.getParameter("startDate");
        String endDateValue = request.getParameter("endDate");
        if (!hasText(title) || !hasText(description) || startDateValue == null || endDateValue == null) {
            throw new IllegalArgumentException("Missing required project fields");
        }

        Date startDate = Date.valueOf(startDateValue);
        Date endDate = Date.valueOf(endDateValue);
        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("Project end date precedes its start date");
        }
        return new Project(0, title.trim(), description.trim(), startDate, endDate, createdBy);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
