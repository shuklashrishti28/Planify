package com.planify.util;

import com.planify.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardRoutesTest {
    @Test
    void routesEachRoleToItsOwnDashboard() {
        assertEquals("admin_dashboard.jsp", DashboardRoutes.pathFor(user("ADMIN")));
        assertEquals("pm_dashboard.jsp", DashboardRoutes.pathFor(user("PROJECT_MANAGER")));
        assertEquals("member", DashboardRoutes.pathFor(user("TEAM_MEMBER")));
    }

    @Test
    void unknownOrMissingUserGoesToLogin() {
        assertEquals("login.jsp", DashboardRoutes.pathFor(null));
        assertEquals("login.jsp", DashboardRoutes.pathFor(user("UNKNOWN")));
    }

    @Test
    void labelsMatchTheRole() {
        assertEquals("Admin dashboard", DashboardRoutes.labelFor(user("ADMIN")));
        assertEquals("PM dashboard", DashboardRoutes.labelFor(user("PROJECT_MANAGER")));
        assertEquals("Member dashboard", DashboardRoutes.labelFor(user("TEAM_MEMBER")));
        assertEquals("Sign in", DashboardRoutes.labelFor(null));
    }

    private static User user(String role) {
        return new User(1, "Test User", "test@example.com", role);
    }
}
