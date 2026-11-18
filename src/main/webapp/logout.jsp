<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Session destroy karke login page par send karega
    if (session != null) {
        session.invalidate();
    }
    response.sendRedirect("login.jsp?msg=loggedout");
%>