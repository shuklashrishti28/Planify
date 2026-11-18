package com.planify.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskServletTest {
    private final TaskServlet servlet = new TaskServlet();

    @Test
    void taskAdminPageRejectsTeamMembers() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context("TEAM_MEMBER");

        servlet.doGet(context.request, context.response);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, context.responseState.status);
    }

    @Test
    void taskPostRejectsInvalidCsrfToken() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context("PROJECT_MANAGER");
        context.parameters.put("csrfToken", "invalid-token");

        servlet.doPost(context.request, context.response);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, context.responseState.status);
        assertEquals("Invalid or expired form token", context.responseState.errorMessage);
    }

    @Test
    void teamMemberCannotUseManagerCreateAction() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context("TEAM_MEMBER");
        context.addCsrfToken();
        context.parameters.put("action", "create");

        servlet.doPost(context.request, context.response);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, context.responseState.status);
    }

    @Test
    void taskPostRejectsUnsupportedStatusBeforeDatabaseAccess() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context("ADMIN");
        context.addCsrfToken();
        context.parameters.put("action", "updateStatus");
        context.parameters.put("taskId", "42");
        context.parameters.put("status", "BLOCKED");

        servlet.doPost(context.request, context.response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.responseState.status);
        assertEquals("Invalid task status", context.responseState.errorMessage);
    }

    @Test
    void taskPostRejectsMalformedProjectId() throws Exception {
        ServletTestSupport.Context context = ServletTestSupport.context("PROJECT_MANAGER");
        context.addCsrfToken();
        context.parameters.put("action", "create");
        context.parameters.put("projectId", "not-a-number");

        servlet.doPost(context.request, context.response);

        assertEquals("tasks?error=invalid", context.responseState.redirect);
    }
}
