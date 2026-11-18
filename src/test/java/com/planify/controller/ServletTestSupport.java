package com.planify.controller;

import com.planify.model.User;
import com.planify.util.CsrfProtection;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class ServletTestSupport {
    private ServletTestSupport() {
    }

    static Context context(String role) {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession session = null;
        if (role != null) {
            attributes.put("currentUser", new User(9, "Test User", "test@example.com", role));
            session = session(attributes);
        }

        Map<String, String> parameters = new HashMap<>();
        ResponseState responseState = new ResponseState();
        HttpSession requestSession = session;
        HttpServletRequest request = proxy(HttpServletRequest.class, (proxy, method, args) -> switch (method.getName()) {
            case "getSession" -> requestSession;
            case "getParameter" -> parameters.get(args[0]);
            case "toString" -> "TestHttpServletRequest";
            default -> defaultValue(method.getReturnType());
        });
        HttpServletResponse response = proxy(HttpServletResponse.class, (proxy, method, args) -> {
            if ("sendRedirect".equals(method.getName())) {
                responseState.redirect = (String) args[0];
            } else if ("sendError".equals(method.getName())) {
                responseState.status = (Integer) args[0];
                responseState.errorMessage = args.length > 1 ? (String) args[1] : null;
            }
            return defaultValue(method.getReturnType());
        });
        return new Context(request, response, session, parameters, responseState);
    }

    private static HttpSession session(Map<String, Object> attributes) {
        return proxy(HttpSession.class, (proxy, method, args) -> switch (method.getName()) {
            case "getAttribute" -> attributes.get(args[0]);
            case "setAttribute" -> {
                attributes.put((String) args[0], args[1]);
                yield null;
            }
            case "removeAttribute" -> {
                attributes.remove(args[0]);
                yield null;
            }
            case "getAttributeNames" -> Collections.enumeration(attributes.keySet());
            case "toString" -> "TestHttpSession";
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == args[0];
            default -> defaultValue(method.getReturnType());
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, java.lang.reflect.InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive() || type == void.class) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0d;
        throw new IllegalArgumentException("Unsupported primitive type: " + type);
    }

    static final class Context {
        final HttpServletRequest request;
        final HttpServletResponse response;
        final HttpSession session;
        final Map<String, String> parameters;
        final ResponseState responseState;

        private Context(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                        Map<String, String> parameters, ResponseState responseState) {
            this.request = request;
            this.response = response;
            this.session = session;
            this.parameters = parameters;
            this.responseState = responseState;
        }

        String addCsrfToken() {
            String token = CsrfProtection.getToken(session);
            parameters.put("csrfToken", token);
            return token;
        }
    }

    static final class ResponseState {
        String redirect;
        Integer status;
        String errorMessage;
    }
}
