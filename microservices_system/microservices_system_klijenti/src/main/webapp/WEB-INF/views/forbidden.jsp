<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>403 Forbidden</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/resources/css/nwtis.css">
</head>
<body>
    <jsp:include page="zaglavlje.jsp"/>

    <div class="container">
        <h1>403 Forbidden</h1>
        <p>Pristup stranici je odbijen zbog manjka prava pristupa.</p>
        <p><a href="<%= request.getContextPath() %>/mvc/pocetak">Povratak na pocetak</a></p>
    </div>
</body>
</html>
