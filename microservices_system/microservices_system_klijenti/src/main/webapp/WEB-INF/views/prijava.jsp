<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Prijava</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/resources/css/nwtis.css">
</head>
<body>
    <jsp:include page="zaglavlje.jsp"/>

    <h1>Prijava</h1>

    <div class="pocetna-centriraj">
         <form class="pocetna-popis" action="j_security_check" method="post">
              <label for="username">Korisničko ime:</label>
              <input class="element-unosa" type="text" id="username" name="j_username" required>
              <label for="password">Lozinka:</label>
              <input class="element-unosa" type="password" id="password" name="j_password" required>
              <input class="gumb" type="submit" value="Prijavi se">
          </form>
    </div>
</body>
</html>
