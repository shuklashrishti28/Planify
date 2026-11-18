package com.planify.util;

import com.planify.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Shared session and role checks for protected servlet routes. */
public final class AccessControl {
    private AccessControl() {}

    /** Redirects unauthenticated users and rejects authenticated users with the wrong role. */
    public static User requireRole(HttpServletRequest request, HttpServletResponse response, String... roles)
            throws IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return null;
        }
        for (String role : roles) {
            if (role.equals(user.getRole())) return user;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to perform this action.");
        return null;
    }
}
