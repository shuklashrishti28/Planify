package com.planify.controller;

import com.planify.dao.ProjectDAO;
import com.planify.model.Project;
import com.planify.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/projects")
public class ProjectServlet extends HttpServlet {
    private ProjectDAO projectDAO;

    @Override
    public void init() {
        projectDAO = new ProjectDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Project> projectList = projectDAO.getAllProjects();
            request.setAttribute("projectList", projectList);
            request.getRequestDispatcher("manage_projects.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("pm_dashboard.jsp?error=db");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        try {
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            Project project = new Project(0, title, description, startDate, endDate, currentUser.getUserId());
            boolean success = projectDAO.createProject(project);

            if (success) {
                response.sendRedirect("projects?msg=created");
            } else {
                response.sendRedirect("projects?error=failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("projects?error=invalid");
        }
    }
}