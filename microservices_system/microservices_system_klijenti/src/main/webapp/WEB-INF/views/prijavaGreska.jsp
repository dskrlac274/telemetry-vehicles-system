<!-- WEB-INF/views/prijavagreska.jsp -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Pogrešna prijava</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/resources/css/nwtis.css">
</head>
<body>
    <jsp:include page="zaglavlje.jsp"/>

    <div class="container">
        <h1>Pogrešna prijava</h1>
        <p>Unijeli ste neispravne podatke za prijavu.</p>
        <p><a href="<%= request.getContextPath() %>/mvc/pocetak">Povratak na pocetak</a></p>
    </div>
</body>
</html>
