<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date, java.text.SimpleDateFormat, edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Kazna" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Detalji kazne</title>
       <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/resources/css/nwtis.css">
    </head>
    <body>
        <jsp:include page="zaglavlje.jsp"/>
        <h1>Detalji kazne</h1>

         <jsp:include page="obavijest.jsp">
             <jsp:param name="statusPrefix" value="Simulacija:" />
             <jsp:param name="statusKljuc" value="simulacija" />
         </jsp:include>

          <%
                  SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
                  Kazna kazna = (Kazna) request.getAttribute("kazna");
                  if (kazna != null) {
                      Date vrijemePocetak = new Date(kazna.getVrijemePocetak());
                      Date vrijemeKraj = new Date(kazna.getVrijemeKraj());
              %>
            <div class="pocetna-centriraj">
              <div class="container">
                  <div class="pocetna-popis">
                  <div class="detalji">
                      <p>
                          <span class="naziv-atributa">Rb:</span> <%= kazna.getRb() %>
                      </p>
                      <p>
                          <span class="naziv-atributa">Id:</span> <%= kazna.getId() %>
                      </p>
                      <p>
                          <span class="naziv-atributa">Vrijeme Početak:</span> <%= sdf.format(vrijemePocetak) %>
                      </p>
                      <p>
                          <span class="naziv-atributa">Vrijeme Kraj:</span> <%= sdf.format(vrijemeKraj) %>
                      </p>
                      <p>
                          <span class="naziv-atributa">Brzina:</span> <%= kazna.getBrzina() %>
                      </p>
                      <p>
                          <span class="naziv-atributa">GPS Širina:</span> <%= kazna.getGpsSirina() %>
                      </p>
                      <p>
                          <span class="naziv-atributa">GPS Dužina:</span> <%= kazna.getGpsDuzina() %>
                      </p>
                      <p>
                          <span class="naziv-atributa">GPS Širina Radara:</span> <%= kazna.getGpsSirinaRadara() %>
                      </p>
                      <p>
                          <span class="naziv-atributa">GPS Dužina Radara:</span> <%= kazna.getGpsDuzinaRadara() %>
                      </p>
                  </div>
                  <%
                      }
                  %>
              </div>
          </div>
    </body>
</html>
