package com.planify.util;

import jakarta.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Creates and validates a per-session synchronizer token for state-changing forms. */
public final class CsrfProtection {
    private static final String SESSION_KEY = CsrfProtection.class.getName() + ".TOKEN";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfProtection() {
    }

    public static String getToken(HttpSession session) {
        synchronized (session) {
            String token = (String) session.getAttribute(SESSION_KEY);
            if (token == null) {
                byte[] randomBytes = new byte[32];
                RANDOM.nextBytes(randomBytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
                session.setAttribute(SESSION_KEY, token);
            }
            return token;
        }
    }

    public static boolean isValid(HttpSession session, String submittedToken) {
        if (session == null || submittedToken == null) {
            return false;
        }
        String expected = (String) session.getAttribute(SESSION_KEY);
        return expected != null && MessageDigest.isEqual(
                expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                submittedToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
