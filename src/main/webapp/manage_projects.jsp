<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.planify.model.Project" %>
<html>
<head>
    <title>Manage Projects - Planify</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; background-color: #f8f9fa; }
        .container { max-width: 900px; margin: auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 15px; }
        input, textarea { width: 100%; padding: 8px; margin-top: 5px; box-sizing: border-box; }
        button { background-color: #007bff; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #0056b3; }
        table { width: 100%; border-collapse: collapse; margin-top: 25px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #343a40; color: white; }
        .alert { padding: 10px; margin-bottom: 15px; border-radius: 4px; background-color: #d4edda; color: #155724; }
    </style>
</head>
<body>
<div class="container">
    <h2>Project Management</h2>
    <a href="pm_dashboard.jsp">← Back to Dashboard</a>
    <hr>

    <% if ("created".equals(request.getParameter("msg"))) { %>
        <div class="alert">Project created successfully!</div>
    <% } %>

    <h3>Create New Project</h3>
    <form action="projects" method="post">
        <div class="form-group">
            <label>Project Title:</label>
            <input type="text" name="title" required />
        </div>
        <div class="form-group">
            <label>Description:</label>
            <textarea name="description" rows="3" required></textarea>
        </div>
        <div class="form-group">
            <label>Start Date:</label>
            <input type="date" name="startDate" required />
        </div>
        <div class="form-group">
            <label>End Date:</label>
            <input type="date" name="endDate" required />
        </div>
        <button type="submit">Create Project</button>
    </form>

    <h3>Existing Projects</h3>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Description</th>
                <th>Start Date</th>
                <th>End Date</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Project> projectList = (List<Project>) request.getAttribute("projectList");
                if (projectList != null && !projectList.isEmpty()) {
                    for (Project p : projectList) {
            %>
            <tr>
                <td><%= p.getProjectId() %></td>
                <td><strong><%= p.getTitle() %></strong></td>
                <td><%= p.getDescription() %></td>
                <td><%= p.getStartDate() %></td>
                <td><%= p.getEndDate() %></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr><td colspan="5">No projects found.</td></tr>
            <% } %>
        </tbody>
    </table>
</div>
</body>
</html>