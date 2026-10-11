<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.planify.model.Task" %>
<%@ page import="com.planify.model.Project" %>
<%@ page import="com.planify.model.User" %>
<html>
<head>
    <title>Manage Tasks - Planify</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; background-color: #f8f9fa; }
        .container { max-width: 950px; margin: auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 15px; }
        input, select, textarea { width: 100%; padding: 8px; margin-top: 5px; box-sizing: border-box; }
        button { background-color: #17a2b8; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #138496; }
        table { width: 100%; border-collapse: collapse; margin-top: 25px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #343a40; color: white; }
        .alert { padding: 10px; margin-bottom: 15px; border-radius: 4px; background-color: #d4edda; color: #155724; }
    </style>
</head>
<body>
<div class="container">
    <h2>Task Management</h2>
    <a href="pm_dashboard.jsp">← Back to PM Dashboard</a> | <a href="admin_dashboard.jsp">Back to Admin Dashboard</a>
    <hr>

    <% if ("created".equals(request.getParameter("msg"))) { %>
        <div class="alert">Task assigned successfully!</div>
    <% } %>

    <h3>Create & Assign Task</h3>
    <form action="tasks" method="post">
        <div class="form-group">
            <label>Select Project:</label>
            <select name="projectId" required>
                <%
                    List<Project> projectList = (List<Project>) request.getAttribute("projectList");
                    if (projectList != null) {
                        for (Project p : projectList) {
                %>
                    <option value="<%= p.getProjectId() %>"><%= p.getTitle() %></option>
                <%      }
                    }
                %>
            </select>
        </div>
        <div class="form-group">
            <label>Assign To User:</label>
            <select name="assignedTo" required>
                <%
                    List<User> userList = (List<User>) request.getAttribute("userList");
                    if (userList != null) {
                        for (User u : userList) {
                %>
                    <option value="<%= u.getUserId() %>"><%= u.getName() %> (<%= u.getRole() %>)</option>
                <%      }
                    }
                %>
            </select>
        </div>
        <div class="form-group">
            <label>Task Title:</label>
            <input type="text" name="title" required />
        </div>
        <div class="form-group">
            <label>Description:</label>
            <textarea name="description" rows="2" required></textarea>
        </div>
        <div class="form-group">
            <label>Initial Status:</label>
            <select name="status">
                <option value="PENDING">PENDING</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
            </select>
        </div>
        <div class="form-group">
            <label>Due Date:</label>
            <input type="date" name="dueDate" required />
        </div>
        <button type="submit">Assign Task</button>
    </form>

    <h3>All Tasks List</h3>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Project</th>
                <th>Task Title</th>
                <th>Assigned To</th>
                <th>Status</th>
                <th>Due Date</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Task> taskList = (List<Task>) request.getAttribute("taskList");
                if (taskList != null && !taskList.isEmpty()) {
                    for (Task t : taskList) {
            %>
            <tr>
                <td><%= t.getTaskId() %></td>
                <td><%= t.getProjectTitle() %></td>
                <td><strong><%= t.getTitle() %></strong></td>
                <td><%= t.getAssignedToName() != null ? t.getAssignedToName() : "Unassigned" %></td>
                <td><strong><%= t.getStatus() %></strong></td>
                <td><%= t.getDueDate() %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr><td colspan="6">No tasks found.</td></tr>
            <% } %>
        </tbody>
    </table>
</div>
</body>
</html>