<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String statusKljuc = request.getParameter("statusKljuc");
    String statusPrefix = request.getParameter("statusPrefix");
    String status = (statusKljuc != null) ? (String) session.getAttribute(statusKljuc) : null;
    if (status != null && statusPrefix != null) {
%>
    <div>
        <p class="status"><%= statusPrefix %> <%= status %></p>
        <%
            session.removeAttribute(statusKljuc);
        %>
    </div>
<%
    }
%>
