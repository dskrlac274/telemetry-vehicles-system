<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Sustav za praćenje i analizu vožnje e-vozilom</title>
       <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/resources/css/nwtis.css">
    </head>
    <body class="pocetna-centriraj">
        <div class="container">
            <h1>Sustav za praćenje i analizu vožnje e-vozilom</h1>
            <div class="pocetna-popis">
                <ul>
                    <li>
                        <a href="${pageContext.servletContext.contextPath}/mvc/kazne">Popis svih kazni</a>
                    </li>
                    <li>
                        <a href="${pageContext.servletContext.contextPath}/mvc/radari">Popis svih radara</a>
                    </li>
                    <li>
                        <a href="${pageContext.servletContext.contextPath}/mvc/vozila">Popis svih pracenih voznji vozila</a>
                    </li>
                    <li>
                        <a href="${pageContext.servletContext.contextPath}/mvc/simulacije">Popis svih voznji vozila</a>
                    </li>
                </ul>
            </div>
        </div>
    </body>
</html>
