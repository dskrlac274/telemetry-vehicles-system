<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Radar" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Detalji radara</title>
     <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/resources/css/nwtis.css">
</head>
<body>
    <jsp:include page="zaglavlje.jsp"/>

    <h1>Detalji radara</h1>

    <jsp:include page="obavijest.jsp">
         <jsp:param name="statusPrefix" value="Simulacija:" />
         <jsp:param name="statusKljuc" value="simulacija" />
     </jsp:include>

    <jsp:include page="obavijest.jsp">
         <jsp:param name="statusPrefix" value="Status provjere radara:" />
          <jsp:param name="statusKljuc" value="status" />
     </jsp:include>

  <%
      Radar radar = (Radar) request.getAttribute("radar");
          if (radar != null) {
  %>
    <div class="pocetna-centriraj">
      <div class="container">
          <div class="pocetna-popis">
          </div>
          <div class="detalji">
              <p>
                  <span class="naziv-atributa">Id:</span> <%= radar.getId() %>
              </p>
              <p>
                  <span class="naziv-atributa">Adresa radara:</span> <%= radar.getAdresaRadara() %>
              </p>
              <p>
                  <span class="naziv-atributa">Mrežna vrata:</span> <%= radar.getMreznaVrataRadara() %>
              </p>
              <p>
                  <span class="naziv-atributa">GPS širina:</span> <%= radar.getGpsSirina() %>
              </p>
              <p>
                  <span class="naziv-atributa">GPS dužina:</span> <%= radar.getGpsDuzina() %>
              </p>
              <p>
                  <span class="naziv-atributa">Maksimalna udaljenost:</span> <%= radar.getMaksUdaljenost() %>
              </p>
          </div>
          <%
              }
          %>
      </div>
  </div>
</body>
</html>
