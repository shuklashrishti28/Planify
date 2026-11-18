<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.planify.model.User" %>
<%@ page import="com.planify.util.HtmlUtil" %>
<%@ include file="/WEB-INF/jspf/csrf.jspf" %>
<html>
<head>
    <title>Manage Users - Planify</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; background-color: #f8f9fa; }
        .container { max-width: 900px; margin: auto; background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 15px; }
        input, select { width: 100%; padding: 8px; margin-top: 5px; box-sizing: border-box; }
        button { background-color: #28a745; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #218838; }
        table { width: 100%; border-collapse: collapse; margin-top: 25px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #007bff; color: white; }
        .alert { padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
    </style>
    <link href="<%= request.getContextPath() %>/app.css" rel="stylesheet">
</head>
<body class="management-page">
<div class="container">
    <h2>User Management</h2>
    <a href="admin_dashboard.jsp">← Back to Dashboard</a>
    <hr>

    <% if (request.getParameter("msg") != null && request.getParameter("msg").matches("created|updated|deleted")) { %>
        <div class="alert success">User <%= HtmlUtil.escape(request.getParameter("msg")) %> successfully.</div>
    <% } else if ("duplicate".equals(request.getParameter("error"))) { %>
        <div class="alert error">Error: Email already exists or DB Error!</div>
    <% } else if (request.getParameter("error") != null) { %><div class="alert error">Please check the submitted values. Passwords must have at least 8 characters.</div><% } %>

    <h3>Add New User</h3>
    <form action="users" method="post"><input type="hidden" name="csrfToken" value="<%= csrfToken %>">
        <div class="form-group">
            <label>Full Name:</label>
            <input type="text" name="name" maxlength="100" required />
        </div>
        <div class="form-group">
            <label>Email Address:</label>
            <input type="email" name="email" maxlength="100" required />
        </div>
        <div class="form-group">
            <label>Password:</label>
            <input type="password" name="password" minlength="8" maxlength="256" required />
        </div>
        <div class="form-group">
            <label>Role:</label>
            <select name="role" required>
                <option value="TEAM_MEMBER">Team Member</option>
                <option value="PROJECT_MANAGER">Project Manager</option>
                <option value="ADMIN">Admin</option>
            </select>
        </div>
        <button type="submit">Create User</button>
    </form>

    <h3>Registered Users</h3>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th><th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<User> userList = (List<User>) request.getAttribute("userList");
                if (userList != null && !userList.isEmpty()) {
                    for (User u : userList) {
            %>
            <tr>
                <td><%= u.getUserId() %></td>
                <td><%= HtmlUtil.escape(u.getName()) %></td>
                <td><%= HtmlUtil.escape(u.getEmail()) %></td>
                <td><strong><%= HtmlUtil.escape(u.getRole()) %></strong></td><td><form action="users" method="post" style="display:flex;gap:6px;align-items:center"><input type="hidden" name="csrfToken" value="<%= csrfToken %>"><input type="hidden" name="action" value="update"><input type="hidden" name="userId" value="<%=u.getUserId()%>"><input name="name" value="<%=HtmlUtil.escape(u.getName())%>" required><input type="email" name="email" value="<%=HtmlUtil.escape(u.getEmail())%>" required><select name="role"><option value="ADMIN" <%= "ADMIN".equals(u.getRole())?"selected":"" %>>Admin</option><option value="PROJECT_MANAGER" <%= "PROJECT_MANAGER".equals(u.getRole())?"selected":"" %>>Project Manager</option><option value="TEAM_MEMBER" <%= "TEAM_MEMBER".equals(u.getRole())?"selected":"" %>>Team Member</option></select><button type="submit">Save</button></form><form action="users" method="post" onsubmit="return confirm('Delete this user? Projects created by them will also be deleted. Continue?');"><input type="hidden" name="csrfToken" value="<%= csrfToken %>"><input type="hidden" name="action" value="delete"><input type="hidden" name="userId" value="<%=u.getUserId()%>"><button type="submit">Delete</button></form></td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr><td colspan="5">No users found.</td></tr>
            <% } %>
        </tbody>
    </table>
</div>
</body>
</html>
