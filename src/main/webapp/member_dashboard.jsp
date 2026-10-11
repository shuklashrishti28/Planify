<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.planify.model.Task" %>
<%@ page import="com.planify.model.User" %>
<%@ page import="com.planify.dao.TaskDAO" %>
<html>
<head>
    <title>Team Member Dashboard - Planify</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<%
    User user = (User) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary px-4 shadow-sm">
    <a class="navbar-brand fw-bold" href="#">Planify Member</a>
    <div class="ms-auto">
        <span class="text-white me-3">Welcome, <strong><%= user.getName() %></strong></span>
        <a href="logout.jsp" class="btn btn-outline-light btn-sm">Logout</a>
    </div>
</nav>

<div class="container mt-5">
    <div class="card border-0 shadow-sm">
        <div class="card-body p-4">
            <h3 class="fw-bold text-primary mb-3">My Assigned Tasks</h3>
            <p class="text-muted">Manage your tasks and update their progress status.</p>
            <hr>

            <div class="table-responsive">
                <table class="table table-hover align-middle mt-3">
                    <thead class="table-dark">
                        <tr>
                            <th>Project</th>
                            <th>Task Title</th>
                            <th>Description</th>
                            <th>Due Date</th>
                            <th>Current Status</th>
                            <th>Update Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            TaskDAO taskDAO = new TaskDAO();
                            List<Task> myTasks = taskDAO.getTasksByUserId(user.getUserId());
                            if (myTasks != null && !myTasks.isEmpty()) {
                                for (Task t : myTasks) {
                                    String badgeClass = "bg-warning text-dark";
                                    if ("IN_PROGRESS".equals(t.getStatus())) {
                                        badgeClass = "bg-info text-white";
                                    } else if ("COMPLETED".equals(t.getStatus())) {
                                        badgeClass = "bg-success text-white";
                                    }
                        %>
                        <tr>
                            <td><strong><%= t.getProjectTitle() %></strong></td>
                            <td><%= t.getTitle() %></td>
                            <td><small class="text-muted"><%= t.getDescription() %></small></td>
                            <td><%= t.getDueDate() %></td>
                            <td>
                                <span class="badge <%= badgeClass %>"><%= t.getStatus() %></span>
                            </td>
                            <td>
                                <form action="tasks" method="post" class="d-flex align-items-center gap-2 m-0">
                                    <input type="hidden" name="action" value="updateStatus" />
                                    <input type="hidden" name="taskId" value="<%= t.getTaskId() %>" />
                                    <input type="hidden" name="redirect" value="member_dashboard.jsp" />

                                    <select name="status" class="form-select form-select-sm" style="width: 140px;">
                                        <option value="PENDING" <%= "PENDING".equals(t.getStatus()) ? "selected" : "" %>>PENDING</option>
                                        <option value="IN_PROGRESS" <%= "IN_PROGRESS".equals(t.getStatus()) ? "selected" : "" %>>IN_PROGRESS</option>
                                        <option value="COMPLETED" <%= "COMPLETED".equals(t.getStatus()) ? "selected" : "" %>>COMPLETED</option>
                                    </select>
                                    <button type="submit" class="btn btn-success btn-sm">Save</button>
                                </form>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="6" class="text-center text-muted py-4">No tasks assigned to you yet.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>