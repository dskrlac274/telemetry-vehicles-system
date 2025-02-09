<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Date, java.text.SimpleDateFormat,
edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Vozila" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Pregled vožnji vozila</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/resources/css/nwtis.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.js"></script>
    <script src="${pageContext.servletContext.contextPath}/resources/js/nwtis.js"></script>
</head>
<body>
    <jsp:include page="zaglavlje.jsp"/>

    <h1>Pregled vožnji praćenih vozila</h1>

    <jsp:include page="obavijest.jsp">
         <jsp:param name="statusPrefix" value="Simulacija:" />
         <jsp:param name="statusKljuc" value="simulacija" />
     </jsp:include>

    <div class="container">
        <jsp:include page="pretrazivanje.jsp">
            <jsp:param name="searchActionUrl" value="${searchActionUrl}" />
        </jsp:include>
    </div>

      <div class="container">
         <h2>Pretraživanje i upravljanje</h2>

        <form action="${pageContext.servletContext.contextPath}/mvc/vozila/pretrazi-po-vozilu" method="post">
            <label for="id">Pretraži vožnje po ID vozila:</label>
            <input class="element-unosa" type="number" id="id" name="id">
            <button class="gumb" type="submit">Pretraži</button>
        </form>

        <form id="vehicleForm" method="post">
            <label for="idVozila">ID Vozila:</label>
            <input class="element-unosa" type="number" id="idVozila" name="idVozila" required>
            <button class="gumb" type="submit" formaction="${pageContext.servletContext.contextPath}/mvc/vozila/pracenje">Praćenje</button>
            <button class="gumb" type="submit" formaction="${pageContext.servletContext.contextPath}/mvc/vozila/zaustavljanje">Zaustavljanje</button>
        </form>

         <jsp:include page="obavijest.jsp">
             <jsp:param name="statusPrefix" value="Status vozila:" />
             <jsp:param name="statusKljuc" value="status" />
         </jsp:include>
    </div>

     <jsp:include page="kreiranjeVozila.jsp">
        <jsp:param name="kreiranjeVozilaUrl" value="${kreiranjeVozilaUrl}" />
    </jsp:include>

    <table id="vanjskiTable">
        <thead>
            <tr>
                <th>Vozilo</th>
                <th>Brzina</th>
                <th>GPS širina</th>
                <th>GPS dužina</th>
                <th>Vrijeme</th>
            </tr>
        </thead>
        <tbody>
            <%
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                List<Vozila> vozila = (List<Vozila>) request.getAttribute("vozila");
                if (vozila != null) {
                    for (Vozila vozilo : vozila) {
                        Date vrijeme = new Date(vozilo.getVrijeme());
            %>
            <tr>
                <td><%= vozilo.getId() %></td>
                <td><%= vozilo.getBrzina() %></td>
                <td><%= vozilo.getGpsSirina() %></td>
                <td><%= vozilo.getGpsDuzina() %></td>
                <td><%= sdf.format(vrijeme) %></td>
            </tr>
            <%
                    }
                }
            %>
        </tbody>
    </table>
     <div>
    </div>
</body>
</html>
