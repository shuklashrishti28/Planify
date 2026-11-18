package com.planify.util;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsrfProtectionTest {
    @Test
    void createsStableUrlSafeTokenForSession() {
        HttpSession session = sessionWithAttributes();

        String first = CsrfProtection.getToken(session);
        String second = CsrfProtection.getToken(session);

        assertEquals(first, second);
        assertTrue(first.matches("[A-Za-z0-9_-]{43}"));
    }

    @Test
    void acceptsOnlyTheTokenStoredForThatSession() {
        HttpSession session = sessionWithAttributes();
        String token = CsrfProtection.getToken(session);

        assertTrue(CsrfProtection.isValid(session, token));
        assertFalse(CsrfProtection.isValid(session, token + "x"));
        assertFalse(CsrfProtection.isValid(session, null));
        assertFalse(CsrfProtection.isValid(null, token));
    }

    private static HttpSession sessionWithAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        return (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class<?>[]{HttpSession.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getAttribute" -> attributes.get(args[0]);
                    case "setAttribute" -> {
                        attributes.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "removeAttribute" -> {
                        attributes.remove(args[0]);
                        yield null;
                    }
                    case "toString" -> "TestHttpSession";
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }
}
