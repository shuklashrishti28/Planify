package com.planify.controller;

import com.planify.dao.ProjectDAO;
import com.planify.dao.TaskDAO;
import com.planify.dao.UserDAO;
import com.planify.model.Project;
import com.planify.model.Task;
import com.planify.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/tasks")
public class TaskServlet extends HttpServlet {
    private TaskDAO taskDAO;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        taskDAO = new TaskDAO();
        projectDAO = new ProjectDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Task> taskList = taskDAO.getAllTasks();
            List<Project> projectList = projectDAO.getAllProjects();
            List<User> userList = userDAO.getAllUsers();

            request.setAttribute("taskList", taskList);
            request.setAttribute("projectList", projectList);
            request.setAttribute("userList", userList);

            request.getRequestDispatcher("manage_tasks.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("pm_dashboard.jsp?error=db");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("updateStatus".equals(action)) {
                int taskId = Integer.parseInt(request.getParameter("taskId"));
                String status = request.getParameter("status");
                taskDAO.updateTaskStatus(taskId, status);

                String redirectUrl = request.getParameter("redirect");
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect("tasks?msg=updated");
                }
            } else {
                int projectId = Integer.parseInt(request.getParameter("projectId"));
                int assignedTo = Integer.parseInt(request.getParameter("assignedTo"));
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String status = request.getParameter("status");
                Date dueDate = Date.valueOf(request.getParameter("dueDate"));

                Task task = new Task(0, projectId, assignedTo, title, description, status, dueDate);
                taskDAO.createTask(task);
                response.sendRedirect("tasks?msg=created");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tasks?error=failed");
        }
    }
}