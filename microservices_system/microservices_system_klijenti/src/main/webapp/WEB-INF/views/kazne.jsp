<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Date, java.text.SimpleDateFormat, edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Kazna" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Pregled kazni</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/resources/css/nwtis.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.js"></script>
    <script src="${pageContext.servletContext.contextPath}/resources/js/nwtis.js"></script>
</head>
<body>
    <jsp:include page="zaglavlje.jsp"/>

    <h1>Pregled kazni</h1>

    <jsp:include page="obavijest.jsp">
         <jsp:param name="statusPrefix" value="Simulacija:" />
         <jsp:param name="statusKljuc" value="simulacija" />
     </jsp:include>

    <jsp:include page="pretrazivanje.jsp">
        <jsp:param name="searchActionUrl" value="${searchActionUrl}" />
    </jsp:include>

    <div class="container">
         <h2>Pretraživanje i upravljanje</h2>

        <form action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazi-po-rednom-broju" method="post">
            <label for="rb">Pretraži kaznu po rednom broju:</label>
            <input class="element-unosa" type="number" id="rb" name="rb">
            <button class="gumb" type="submit">Pretraži</button>
        </form>

        <form action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazi-po-vozilu" method="post">
            <label for="id">Pretraži kazne po ID vozila:</label>
            <input class="element-unosa" type="number" id="id" name="id">
            <button class="gumb" type="submit">Pretraži</button>
        </form>

        <form action="${pageContext.servletContext.contextPath}/mvc/kazne/provjeri" method="post" id="provjeriForm">
            <button class="gumb" type="submit" id="provjeriButton">Provjeri PosluziteljaKazni</button>
        </form>

     <jsp:include page="obavijest.jsp">
         <jsp:param name="statusPrefix" value="Status PosluziteljaKazni:" />
         <jsp:param name="statusKljuc" value="status" />
     </jsp:include>
    </div>

    <table id="vanjskiTable">
        <thead>
            <tr>
                <th>R.br.</th>
                <th>Id</th>
                <th>Vrijeme izdavanja</th>
                <th>Brzina</th>
                <th>GPS širina</th>
                <th>GPS dužina</th>
            </tr>
        </thead>
        <tbody>
            <%
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
                List<Kazna> kazne = (List<Kazna>) request.getAttribute("kazne");

                if (kazne != null) {
                    for (Kazna k : kazne) {
                        Date vrijeme = new Date(k.getVrijemeKraj());
            %>
            <tr>
                <td><%= k.getRb() %></td>
                <td><%= k.getId() %></td>
                <td><%= sdf.format(vrijeme) %></td>
                <td><%= k.getBrzina() %></td>
                <td><%= k.getGpsSirina() %></td>
                <td><%= k.getGpsDuzina() %></td>
            </tr>
            <%
                    }
                }
            %>
        </tbody>
    </table>
</body>
</html>
