<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.planify.model.Project" %>
<%@ page import="com.planify.model.User" %>
<%@ page import="com.planify.util.DashboardRoutes" %>
<%@ page import="com.planify.util.HtmlUtil" %>
<%@ include file="/WEB-INF/jspf/csrf.jspf" %>
<% User dashboardUser = (User) session.getAttribute("currentUser"); %>
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
    <link href="<%= request.getContextPath() %>/app.css" rel="stylesheet">
</head>
<body class="management-page">
<div class="container">
    <h2>Project Management</h2>
    <a href="<%= HtmlUtil.escape(DashboardRoutes.pathFor(dashboardUser)) %>">← Back to Dashboard</a>
    <hr>

    <% if (request.getParameter("msg") != null && request.getParameter("msg").matches("created|updated|deleted")) { %>
        <div class="alert">Project <%= HtmlUtil.escape(request.getParameter("msg")) %> successfully.</div>
    <% } %>

    <h3>Create New Project</h3>
    <form action="projects" method="post"><input type="hidden" name="csrfToken" value="<%= csrfToken %>">
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
                <th>End Date</th><th>Actions</th>
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
                <td><strong><%= HtmlUtil.escape(p.getTitle()) %></strong></td><td><%= HtmlUtil.escape(p.getDescription()) %></td><td><%= p.getStartDate() %></td><td><%= p.getEndDate() %></td><td><form action="projects" method="post" style="display:flex;gap:4px;align-items:center"><input type="hidden" name="csrfToken" value="<%= csrfToken %>"><input type="hidden" name="action" value="update"><input type="hidden" name="projectId" value="<%=p.getProjectId()%>"><input name="title" value="<%=HtmlUtil.escape(p.getTitle())%>" required><input name="description" value="<%=HtmlUtil.escape(p.getDescription())%>" required><input type="date" name="startDate" value="<%=p.getStartDate()%>" required><input type="date" name="endDate" value="<%=p.getEndDate()%>" required><button type="submit">Save</button></form><form action="projects" method="post" onsubmit="return confirm('Delete this project and its tasks?');"><input type="hidden" name="csrfToken" value="<%= csrfToken %>"><input type="hidden" name="action" value="delete"><input type="hidden" name="projectId" value="<%=p.getProjectId()%>"><button type="submit">Delete</button></form></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr><td colspan="6">No projects found.</td></tr>
            <% } %>
        </tbody>
    </table>
</div>
</body>
</html>
