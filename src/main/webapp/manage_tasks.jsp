<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.planify.model.Task" %>
<%@ page import="com.planify.model.Project" %>
<%@ page import="com.planify.model.User" %>
<%@ page import="com.planify.util.DashboardRoutes" %>
<%@ page import="com.planify.util.HtmlUtil" %>
<%@ include file="/WEB-INF/jspf/csrf.jspf" %>
<%
    java.util.Map<String, Integer> taskCounts =
            (java.util.Map<String, Integer>) request.getAttribute("taskCounts");
    int pendingCount = taskCounts == null || taskCounts.get("PENDING") == null ? 0 : taskCounts.get("PENDING");
    int inProgressCount = taskCounts == null || taskCounts.get("IN_PROGRESS") == null ? 0 : taskCounts.get("IN_PROGRESS");
    int completedCount = taskCounts == null || taskCounts.get("COMPLETED") == null ? 0 : taskCounts.get("COMPLETED");
    int totalTasks = pendingCount + inProgressCount + completedCount;
    int pendingPercent = totalTasks == 0 ? 0 : pendingCount * 100 / totalTasks;
    int inProgressPercent = totalTasks == 0 ? 0 : inProgressCount * 100 / totalTasks;
    int completedPercent = totalTasks == 0 ? 0 : 100 - pendingPercent - inProgressPercent;
    User dashboardUser = (User) session.getAttribute("currentUser");
    String dashboardPath = DashboardRoutes.pathFor(dashboardUser);
    String dashboardLabel = DashboardRoutes.labelFor(dashboardUser);
%>
<html>
<head>
    <title>Manage Tasks - Planify</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        :root { color-scheme: light; --ink:#172033; --muted:#667085; --line:#e6eaf0; --panel:#fff; --canvas:#f4f7fb; --brand:#4f46e5; --pending:#f59e0b; --progress:#3b82f6; --done:#10b981; }
        * { box-sizing: border-box; }
        body { margin:0; padding:32px 20px; background:var(--canvas); color:var(--ink); font:15px/1.5 Inter,Segoe UI,Arial,sans-serif; }
        .container { max-width:1180px; margin:auto; }
        .page-header { display:flex; align-items:center; justify-content:space-between; gap:16px; margin-bottom:24px; }
        h1,h2,h3,p { margin-top:0; }
        h1 { margin-bottom:4px; font-size:clamp(25px,4vw,34px); letter-spacing:-.04em; }
        h2 { font-size:20px; margin-bottom:4px; }
        h3 { margin:0 0 16px; font-size:17px; }
        .subheading,.muted { color:var(--muted); }
        .nav-links { display:flex; flex-wrap:wrap; gap:8px; }
        .nav-links a { color:#4338ca; background:#eef2ff; border-radius:9px; padding:9px 12px; text-decoration:none; font-weight:600; }
        .nav-links a:hover { background:#e0e7ff; }
        .panel { background:var(--panel); border:1px solid var(--line); border-radius:16px; padding:22px; box-shadow:0 8px 24px rgba(31,41,55,.04); }
        .progress-section { margin-bottom:22px; }
        .section-heading { display:flex; justify-content:space-between; align-items:end; gap:10px; margin-bottom:16px; }
        .section-heading h2 { margin:0; }
        .chart-grid { display:grid; grid-template-columns:minmax(220px,.8fr) minmax(320px,1.4fr); gap:16px; }
        .chart-card { min-height:245px; }
        .chart-card h3 { margin-bottom:4px; }
        .chart-caption { color:var(--muted); font-size:13px; margin-bottom:18px; }
        .donut-wrap { display:flex; align-items:center; justify-content:center; gap:24px; min-height:160px; }
        .donut { width:150px; height:150px; flex:none; position:relative; border-radius:50%; background:conic-gradient(var(--pending) 0 <%= pendingPercent %>%, var(--progress) <%= pendingPercent %>% <%= pendingPercent + inProgressPercent %>%, var(--done) <%= pendingPercent + inProgressPercent %>% 100%); }
        .donut::after { content:""; position:absolute; inset:19px; border-radius:50%; background:var(--panel); }
        .donut-empty { background:conic-gradient(#e8edf4 0 100%); }
        .donut-center { position:absolute; z-index:1; inset:0; display:flex; flex-direction:column; align-items:center; justify-content:center; line-height:1.15; }
        .donut-center strong { font-size:28px; letter-spacing:-.04em; }
        .donut-center span { color:var(--muted); font-size:12px; }
        .legend { display:grid; gap:11px; min-width:125px; }
        .legend-item { display:flex; align-items:center; gap:8px; color:var(--muted); font-size:13px; }
        .legend-item strong { margin-left:auto; color:var(--ink); }
        .dot { width:9px; height:9px; border-radius:50%; }
        .dot.pending { background:var(--pending); } .dot.progress { background:var(--progress); } .dot.done { background:var(--done); }
        .bar-chart { display:flex; align-items:stretch; gap:14px; height:145px; padding:8px 12px 0; border-bottom:1px solid var(--line); }
        .bar-item { flex:1; display:flex; flex-direction:column; align-items:center; justify-content:flex-end; gap:7px; min-width:0; }
        .bar-value { color:var(--ink); font-weight:700; font-size:13px; }
        .bar-track { height:100px; width:min(56px,75%); display:flex; align-items:flex-end; }
        .bar { width:100%; min-height:0; border-radius:8px 8px 2px 2px; transition:height .35s ease; }
        .bar.pending { background:linear-gradient(180deg,#fbbf24,var(--pending)); }
        .bar.progress { background:linear-gradient(180deg,#60a5fa,var(--progress)); }
        .bar.done { background:linear-gradient(180deg,#34d399,var(--done)); }
        .bar-label { color:var(--muted); font-size:12px; white-space:nowrap; }
        .completion { margin-top:17px; }
        .completion-label { display:flex; justify-content:space-between; gap:12px; font-size:13px; margin-bottom:7px; }
        .completion-label span { color:var(--muted); }
        .progress-track { height:9px; background:#e9eef5; border-radius:99px; overflow:hidden; }
        .progress-fill { height:100%; background:linear-gradient(90deg,#34d399,var(--done)); border-radius:inherit; }
        .content-grid { display:grid; grid-template-columns:minmax(280px,.85fr) minmax(0,1.6fr); align-items:start; gap:18px; }
        .form-group { margin-bottom:14px; }
        label { display:block; color:#344054; font-size:13px; font-weight:600; margin-bottom:10px; }
        input,select,textarea { display:block; width:100%; padding:10px 11px; margin-top:5px; border:1px solid #d5dce7; border-radius:9px; background:white; color:var(--ink); font:inherit; }
        textarea { resize:vertical; min-height:74px; }
        input:focus,select:focus,textarea:focus { outline:3px solid #c7d2fe; border-color:var(--brand); }
        button { background:var(--brand); color:white; padding:10px 14px; border:0; border-radius:9px; cursor:pointer; font:inherit; font-weight:700; }
        button:hover { background:#4338ca; }
        table { width:100%; border-collapse:collapse; margin-top:14px; }
        th,td { border-bottom:1px solid var(--line); padding:11px 9px; text-align:left; vertical-align:top; }
        th { color:var(--muted); font-size:11px; text-transform:uppercase; letter-spacing:.06em; background:#f8fafc; }
        .table-wrap { overflow-x:auto; }
        .alert { padding:12px 14px; margin-bottom:16px; border-radius:10px; background:#ecfdf3; color:#067647; border:1px solid #abefc6; }
        details { margin-bottom:8px; }
        summary { color:#4338ca; cursor:pointer; font-weight:700; }
        details form { min-width:230px; padding:10px 0; }
        .delete-button { background:#fff1f0; color:#b42318; padding:7px 10px; }
        .delete-button:hover { background:#fee4e2; }
        @media (max-width:820px) { .chart-grid,.content-grid { grid-template-columns:1fr; } .page-header { align-items:flex-start; flex-direction:column; } }
        @media (max-width:520px) { body { padding:20px 12px; } .panel { padding:16px; } .donut-wrap { gap:14px; } .donut { width:128px; height:128px; } .legend { min-width:110px; } }
    </style>
    <link href="<%= request.getContextPath() %>/app.css" rel="stylesheet">
</head>
<body class="tasks-page">
<div class="container">
    <header class="page-header">
        <div><h1>Tasks &amp; Progress</h1><p class="subheading">Plan work, track status, and see how the team is progressing.</p></div>
        <nav class="nav-links" aria-label="Dashboard navigation"><a href="<%= HtmlUtil.escape(dashboardPath) %>"><%= HtmlUtil.escape(dashboardLabel) %></a></nav>
    </header>

    <% if ("created".equals(request.getParameter("msg")) || "updated".equals(request.getParameter("msg")) || "deleted".equals(request.getParameter("msg"))) { %>
        <div class="alert">Task <%= HtmlUtil.escape(request.getParameter("msg")) %> successfully.</div>
    <% } %>

    <section class="progress-section" aria-label="Task progress charts">
        <div class="section-heading"><div><h2>Project progress</h2><span class="muted">A live overview of all task statuses</span></div></div>
        <div class="chart-grid">
            <article class="panel chart-card">
                <h3>Task distribution</h3><p class="chart-caption">Share of tasks by current status</p>
                <div class="donut-wrap">
                    <div class="donut <%= totalTasks == 0 ? "donut-empty" : "" %>" role="img" aria-label="Task distribution: <%= pendingCount %> pending, <%= inProgressCount %> in progress, <%= completedCount %> completed">
                        <div class="donut-center"><strong><%= totalTasks %></strong><span>Total tasks</span></div>
                    </div>
                    <div class="legend" aria-label="Chart legend">
                        <div class="legend-item"><i class="dot pending"></i>Pending <strong><%= pendingCount %></strong></div>
                        <div class="legend-item"><i class="dot progress"></i>In progress <strong><%= inProgressCount %></strong></div>
                        <div class="legend-item"><i class="dot done"></i>Completed <strong><%= completedCount %></strong></div>
                    </div>
                </div>
            </article>
            <article class="panel chart-card">
                <h3>Tasks by status</h3><p class="chart-caption">Compare workload and completed items</p>
                <div class="bar-chart" role="img" aria-label="Task counts: pending <%= pendingCount %>, in progress <%= inProgressCount %>, completed <%= completedCount %>">
                    <div class="bar-item"><span class="bar-value"><%= pendingCount %></span><div class="bar-track"><div class="bar pending" style="height:<%= pendingCount == 0 ? 0 : Math.max(8, pendingPercent) %>%"></div></div><span class="bar-label">Pending</span></div>
                    <div class="bar-item"><span class="bar-value"><%= inProgressCount %></span><div class="bar-track"><div class="bar progress" style="height:<%= inProgressCount == 0 ? 0 : Math.max(8, inProgressPercent) %>%"></div></div><span class="bar-label">In progress</span></div>
                    <div class="bar-item"><span class="bar-value"><%= completedCount %></span><div class="bar-track"><div class="bar done" style="height:<%= completedCount == 0 ? 0 : Math.max(8, completedPercent) %>%"></div></div><span class="bar-label">Completed</span></div>
                </div>
                <div class="completion"><div class="completion-label"><strong>Overall completion</strong><span><%= completedPercent %>%</span></div><div class="progress-track" role="progressbar" aria-label="Overall task completion" aria-valuemin="0" aria-valuemax="100" aria-valuenow="<%= completedPercent %>"><div class="progress-fill" style="width:<%= completedPercent %>%"></div></div></div>
            </article>
        </div>
    </section>

    <div class="content-grid">
    <section class="panel">
    <h2>Create &amp; assign task</h2><p class="chart-caption">Give a team member a clear next step.</p>
    <form action="tasks" method="post"><input type="hidden" name="csrfToken" value="<%= csrfToken %>">
        <input type="hidden" name="action" value="create">
        <div class="form-group">
            <label>Select Project:</label>
            <select name="projectId" required>
                <%
                    List<Project> projectList = (List<Project>) request.getAttribute("projectList");
                    if (projectList != null) {
                        for (Project p : projectList) {
                %>
                    <option value="<%= p.getProjectId() %>"><%= HtmlUtil.escape(p.getTitle()) %></option>
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
                    <option value="<%= u.getUserId() %>"><%= HtmlUtil.escape(u.getName()) %> (<%= HtmlUtil.escape(u.getRole()) %>)</option>
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
        <button type="submit">Assign task</button>
    </form>
    </section>

    <section class="panel">
    <div class="section-heading"><div><h2>All tasks</h2><span class="muted"><%= totalTasks %> task<%= totalTasks == 1 ? "" : "s" %> across all projects</span></div></div>
    <div class="table-wrap">
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Project</th>
                <th>Task Title</th>
                <th>Assigned To</th>
                <th>Status</th>
                <th>Due Date</th>
                <th>Actions</th>
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
                <td><%= HtmlUtil.escape(t.getProjectTitle()) %></td>
                <td><strong><%= HtmlUtil.escape(t.getTitle()) %></strong></td>
                <td><%= HtmlUtil.escape(t.getAssignedToName() != null ? t.getAssignedToName() : "Unassigned") %></td>
                <td><strong><%= t.getStatus() %></strong></td>
                <td><%= t.getDueDate() %></td>
                <td>
                    <details><summary>Edit</summary>
                        <form action="tasks" method="post">
                            <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="taskId" value="<%= t.getTaskId() %>">
                            <label>Project <select name="projectId" required>
                                <% for (Project p : projectList) { %><option value="<%= p.getProjectId() %>" <%= p.getProjectId() == t.getProjectId() ? "selected" : "" %>><%= HtmlUtil.escape(p.getTitle()) %></option><% } %>
                            </select></label>
                            <label>Assignee <select name="assignedTo" required>
                                <% for (User u : userList) { %><option value="<%= u.getUserId() %>" <%= u.getUserId() == t.getAssignedTo() ? "selected" : "" %>><%= HtmlUtil.escape(u.getName()) %></option><% } %>
                            </select></label>
                            <label>Title <input name="title" maxlength="150" value="<%= HtmlUtil.escape(t.getTitle()) %>" required></label>
                            <label>Description <textarea name="description" required><%= HtmlUtil.escape(t.getDescription()) %></textarea></label>
                            <label>Status <select name="status"><option value="PENDING" <%= "PENDING".equals(t.getStatus()) ? "selected" : "" %>>Pending</option><option value="IN_PROGRESS" <%= "IN_PROGRESS".equals(t.getStatus()) ? "selected" : "" %>>In progress</option><option value="COMPLETED" <%= "COMPLETED".equals(t.getStatus()) ? "selected" : "" %>>Completed</option></select></label>
                            <label>Due date <input type="date" name="dueDate" value="<%= t.getDueDate() %>" required></label>
                            <button type="submit">Save task</button>
                        </form>
                    </details>
                    <form action="tasks" method="post" onsubmit="return confirm('Delete this task?');">
                        <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="taskId" value="<%= t.getTaskId() %>">
                        <button class="delete-button" type="submit">Delete</button>
                    </form>
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr><td colspan="7">No tasks found.</td></tr>
            <% } %>
        </tbody>
    </table>
    </div>
    </section>
    </div>
</div>
</body>
</html>
