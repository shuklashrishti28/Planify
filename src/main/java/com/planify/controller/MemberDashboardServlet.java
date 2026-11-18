package com.planify.controller;

import com.planify.dao.TaskDAO;
import com.planify.model.User;
import com.planify.util.AccessControl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Prepares the signed-in team member's dashboard data. */
@WebServlet("/member")
public class MemberDashboardServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(MemberDashboardServlet.class.getName());
    private TaskDAO taskDAO;

    @Override
    public void init() {
        taskDAO = new TaskDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = AccessControl.requireRole(request, response, "TEAM_MEMBER");
        if (user == null) {
            return;
        }

        try {
            request.setAttribute("myTasks", taskDAO.getTasksByUserId(user.getUserId()));
            request.getRequestDispatcher("member_dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to load the team member dashboard", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load your tasks. Please try again later.");
        }
    }
}
