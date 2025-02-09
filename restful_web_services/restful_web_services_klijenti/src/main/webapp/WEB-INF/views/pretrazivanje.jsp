<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String actionUrl = (String) request.getAttribute("searchActionUrl");
%>
 <div class="centriraj-razmak">
    <div>
        <h2>Pretraživanje u intervalu</h2>
        <form id="forma-pretrazivanje" method="get" action="<%= actionUrl %>">
            <table>
            <tbody class="forma-elementi-centriraj">
                <tr>
                    <td>Od vremena: </td>
                    <td>
                        <input class="element-unosa" id="odVremena" type="datetime-local" required/>
                        <input name="odVremena" type="hidden" required/>
                    </td>
                </tr>
                <tr>
                    <td>Do vremena: </td>
                    <td>
                        <input class="element-unosa" id="doVremena" type="datetime-local" required/>
                        <input name="doVremena" type="hidden" required/>
                    </td>
                </tr>
                <tr>
                    <td><input class="gumb" type="submit" value="Dohvati"></td>
                </tr>
            </tbody>
            </table>
        </form>
    </div>
</div>
