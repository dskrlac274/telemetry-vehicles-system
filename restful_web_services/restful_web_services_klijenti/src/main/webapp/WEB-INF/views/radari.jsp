<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Radar" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Pregled radara</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/resources/css/nwtis.css">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.js"></script>
    <script src="${pageContext.servletContext.contextPath}/resources/js/nwtis.js"></script>
</head>
<body>
    <jsp:include page="zaglavlje.jsp"/>

    <h1>Pregled radara</h1>

    <jsp:include page="obavijest.jsp">
         <jsp:param name="statusPrefix" value="Simulacija:" />
         <jsp:param name="statusKljuc" value="simulacija" />
     </jsp:include>

    <div class="container">
         <h2>Pretraživanje i upravljanje</h2>

        <form action="${pageContext.servletContext.contextPath}/mvc/radari/pretrazi-po-id" method="post">
            <label for="id">Pretraži radara po ID:</label>
            <input class="element-unosa" type="number" id="id" name="id">
            <button class="gumb" type="submit">Pretraži</button>
        </form>

        <div class="centriraj-razmak">
            <form action="${pageContext.servletContext.contextPath}/mvc/radari/obrisiSve" method="post" id="deleteAllForm">
                <button class="gumb" type="submit" id="deleteAllButton">Obriši sve radare</button>
            </form>
            <form action="${pageContext.servletContext.contextPath}/mvc/radari/reset" method="post" id="resetForm">
                <button class="gumb" type="submit" id="resetButton">Resetiraj radare</button>
            </form>
        </div>

         <jsp:include page="obavijest.jsp">
             <jsp:param name="statusPrefix" value="Status radara:" />
            <jsp:param name="statusKljuc" value="status" />
         </jsp:include>
        </div>

    <table id="vanjskiTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Adresa radara</th>
                <th>Mrežna vrata</th>
                <th>GPS širina</th>
                <th>GPS dužina</th>
                <th>Maksimalna udaljenost</th>
                <th>Akcije</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Radar> radari = (List<Radar>) request.getAttribute("radari");
                if (radari != null) {
                    for (Radar radar : radari) {
            %>
            <tr>
                <td><%= radar.getId() %></td>
                <td><%= radar.getAdresaRadara() %></td>
                <td><%= radar.getMreznaVrataRadara() %></td>
                <td><%= radar.getGpsSirina() %></td>
                <td><%= radar.getGpsDuzina() %></td>
                <td><%= radar.getMaksUdaljenost() %></td>
                <td class="centriraj-razmak">
                    <form action="${pageContext.servletContext.contextPath}/mvc/radari/<%= radar.getId() %>/obrisi"
                     method="post">
                        <button class="gumb" type="submit" class="delete-button">Obriši</button>
                    </form>
                    <form action="${pageContext.servletContext.contextPath}/mvc/radari/<%= radar.getId() %>"
                     method="get">
                        <button class="gumb" type="submit" class="provjeri-button">Provjeri</button>
                    </form>
                </td>
            </tr>
            <%
                    }
                }
            %>
        </tbody>
    </table>
</body>
</html>
