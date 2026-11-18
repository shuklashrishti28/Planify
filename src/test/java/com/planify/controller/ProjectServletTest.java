package com.planify.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectServletTest {
    private final ProjectServlet servlet = new ProjectServlet();

    @Test
    void projectPageRedirectsUnauthenticatedRequests() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context(null);

        servlet.doGet(context.request, context.response);

        assertEquals("login.jsp", context.responseState.redirect);
    }

    @Test
    void projectPageRejectsTeamMembers() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context("TEAM_MEMBER");

        servlet.doGet(context.request, context.response);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, context.responseState.status);
        assertEquals("You do not have permission to perform this action.", context.responseState.errorMessage);
    }

    @Test
    void projectPostRejectsInvalidCsrfToken() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context("ADMIN");
        context.parameters.put("csrfToken", "invalid-token");

        servlet.doPost(context.request, context.response);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, context.responseState.status);
        assertEquals("Invalid or expired form token", context.responseState.errorMessage);
    }

    @Test
    void projectPostRejectsEndDateBeforeStartDate() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context("PROJECT_MANAGER");
        context.addCsrfToken();
        context.parameters.put("action", "create");
        context.parameters.put("title", "Planning");
        context.parameters.put("description", "Milestone plan");
        context.parameters.put("startDate", "2026-06-20");
        context.parameters.put("endDate", "2026-06-19");

        servlet.doPost(context.request, context.response);

        assertEquals("projects?error=invalid", context.responseState.redirect);
    }

    @Test
    void projectPostRejectsMissingRequiredFields() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context("ADMIN");
        context.addCsrfToken();
        context.parameters.put("action", "create");
        context.parameters.put("title", " ");
        context.parameters.put("description", "Description");
        context.parameters.put("startDate", "2026-06-01");
        context.parameters.put("endDate", "2026-06-02");

        servlet.doPost(context.request, context.response);

        assertEquals("projects?error=invalid", context.responseState.redirect);
    }
}
