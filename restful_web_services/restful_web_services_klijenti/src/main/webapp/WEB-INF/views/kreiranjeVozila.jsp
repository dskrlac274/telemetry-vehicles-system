<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Vozila" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%
    String url = (String) request.getAttribute("kreiranjeVozilaUrl");
%>

<div>
    <h2>Kreiraj Novu vožnju</h2>
    <form method="post" action="<%= url %>" class="forma-grid">
        <div class="forma-stupac">
            <label for="id">ID Vozila:</label>
            <input class="element-unosa" id="id" name="id" required>

            <label for="broj">Broj Vozila:</label>
            <input class="element-unosa" id="broj" name="broj" required>

            <label for="vrijeme">Vrijeme:</label>
            <input class="element-unosa" id="vrijeme" name="vrijeme" required>

            <label for="brzina">Brzina:</label>
            <input class="element-unosa" id="brzina" name="brzina" required>

            <label for="snaga">Snaga:</label>
            <input class="element-unosa" id="snaga" name="snaga" required>

            <label for="struja">Struja:</label>
            <input class="element-unosa" id="struja" name="struja" required>
        </div>

        <div class="forma-stupac">
            <label for="visina">Visina:</label>
            <input class="element-unosa" id="visina" name="visina" required>

            <label for="gpsBrzina">GPS Brzina:</label>
            <input class="element-unosa" id="gpsBrzina" name="gpsBrzina" required>

            <label for="tempVozila">Temp Vozila:</label>
            <input class="element-unosa" id="tempVozila" name="tempVozila" required>

            <label for="postotakBaterija">Postotak Baterija:</label>
            <input class="element-unosa" id="postotakBaterija" name="postotakBaterija" required>

            <label for="naponBaterija">Napon Baterija:</label>
            <input class="element-unosa" id="naponBaterija" name="naponBaterija" required>

            <label for="kapacitetBaterija">Kapacitet Baterija:</label>
            <input class="element-unosa" id="kapacitetBaterija" name="kapacitetBaterija" required>
        </div>

        <div class="forma-stupac">
            <label for="tempBaterija">Temp Baterija:</label>
            <input class="element-unosa" id="tempBaterija" name="tempBaterija" required>

            <label for="preostaloKm">Preostalo Km:</label>
            <input class="element-unosa" id="preostaloKm" name="preostaloKm" required>

            <label for="ukupnoKm">Ukupno Km:</label>
            <input class="element-unosa" id="ukupnoKm" name="ukupnoKm" required>

            <label for="gpsSirina">GPS Širina:</label>
            <input class="element-unosa" id="gpsSirina" name="gpsSirina" required>

            <label for="gpsDuzina">GPS Dužina:</label>
            <input class="element-unosa" id="gpsDuzina" name="gpsDuzina" required>

            <label class="skriveno">Dodaj vozilo</label>
            <button class="gumb" type="submit">Kreiraj</button>
        </div>
    </form>

     <jsp:include page="obavijest.jsp">
         <jsp:param name="statusPrefix" value="" />
         <jsp:param name="statusKljuc" value="statusKreiranjaVozila" />
     </jsp:include>
</div>
