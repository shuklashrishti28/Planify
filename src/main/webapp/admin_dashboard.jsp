<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.planify.model.User" %>
<%@ page import="com.planify.util.HtmlUtil" %>
<% User dashboardUser=(User)session.getAttribute("currentUser"); if(dashboardUser==null){response.sendRedirect("login.jsp");return;} if(!"ADMIN".equals(dashboardUser.getRole())){response.sendError(403);return;} %>
<html>
<head>
    <title>Admin Dashboard - Planify</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/app.css" rel="stylesheet">
</head>
<body class="bg-light dashboard-page">
<nav class="navbar navbar-expand-lg navbar-dark bg-primary px-4 shadow-sm">
    <a class="navbar-brand fw-bold" href="#">Planify Admin</a>
    <div class="ms-auto">
        <span class="text-white me-3">Welcome, <strong><%= HtmlUtil.escape(dashboardUser.getName()) %></strong></span>
        <a href="logout.jsp" class="btn btn-outline-light btn-sm">Logout</a>
    </div>
</nav>

<div class="container mt-5">
    <div class="row">
        <div class="col-md-4">
            <div class="card border-0 shadow-sm">
                <div class="card-body text-center p-4">
                    <h4 class="card-title fw-bold">User Management</h4>
                    <p class="card-text text-muted">Add users, update roles, and manage system access.</p>
                    <a href="users" class="btn btn-primary px-4">Manage Users</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm">
                <div class="card-body text-center p-4">
                    <h4 class="card-title fw-bold">Projects</h4>
                    <p class="card-text text-muted">Create and maintain project timelines.</p>
                    <a href="projects" class="btn btn-success px-4">Manage Projects</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card border-0 shadow-sm">
                <div class="card-body text-center p-4">
                    <h4 class="card-title fw-bold">Tasks & Progress</h4>
                    <p class="card-text text-muted">Assign tasks and review completion status.</p>
                    <a href="tasks" class="btn btn-info text-white px-4">Manage Tasks</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
