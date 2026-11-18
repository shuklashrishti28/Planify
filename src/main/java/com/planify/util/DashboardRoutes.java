package com.planify.util;

import com.planify.model.User;

/** Centralizes the dashboard destination and label for each signed-in role. */
public final class DashboardRoutes {
    private DashboardRoutes() {
    }

    public static String pathFor(User user) {
        if (user == null) {
            return "login.jsp";
        }
        return switch (user.getRole()) {
            case "ADMIN" -> "admin_dashboard.jsp";
            case "PROJECT_MANAGER" -> "pm_dashboard.jsp";
            case "TEAM_MEMBER" -> "member";
            default -> "login.jsp";
        };
    }

    public static String labelFor(User user) {
        if (user == null) {
            return "Sign in";
        }
        return switch (user.getRole()) {
            case "ADMIN" -> "Admin dashboard";
            case "PROJECT_MANAGER" -> "PM dashboard";
            case "TEAM_MEMBER" -> "Member dashboard";
            default -> "Sign in";
        };
    }
}
