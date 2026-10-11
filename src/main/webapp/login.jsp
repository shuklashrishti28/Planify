<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Planify - Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center vh-100">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card shadow-lg border-0 rounded-3">
                <div class="card-body p-4">
                    <h3 class="text-center fw-bold text-primary mb-3">Planify</h3>
                    <p class="text-center text-muted mb-4">Project Management System</p>

                    <% if ("invalid".equals(request.getParameter("error"))) { %>
                        <div class="alert alert-danger py-2 fs-6 text-center">Invalid Email or Password!</div>
                    <% } else if ("loggedout".equals(request.getParameter("msg"))) { %>
                        <div class="alert alert-success py-2 fs-6 text-center">Successfully Logged Out!</div>
                    <% } %>

                    <form action="login" method="post">
                        <div class="mb-3">
                            <label class="form-label font-weight-bold">Email Address</label>
                            <input type="email" name="email" class="form-control" placeholder="name@planify.com" required />
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Password</label>
                            <input type="password" name="password" class="form-control" placeholder="••••••••" required />
                        </div>
                        <button type="submit" class="btn btn-primary w-100 py-2 mt-2 fw-bold">Login</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>