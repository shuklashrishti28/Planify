<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>PM Dashboard - Planify</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-success px-4 shadow-sm">
    <a class="navbar-brand fw-bold" href="#">Planify PM</a>
    <div class="ms-auto">
        <span class="text-white me-3">Welcome, <strong>${currentUser.name}</strong></span>
        <a href="logout.jsp" class="btn btn-outline-light btn-sm">Logout</a>
    </div>
</nav>

<div class="container mt-5">
    <div class="row g-4">
        <div class="col-md-5">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body p-4">
                    <h4 class="card-title fw-bold text-success">Projects</h4>
                    <p class="card-text text-muted">Create projects, set timelines, and track progress.</p>
                    <a href="projects" class="btn btn-success px-4">Manage Projects</a>
                </div>
            </div>
        </div>
        <div class="col-md-5">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body p-4">
                    <h4 class="card-title fw-bold text-info">Tasks</h4>
                    <p class="card-text text-muted">Assign tasks to team members and monitor status.</p>
                    <a href="tasks" class="btn btn-info text-white px-4">Manage Tasks</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>