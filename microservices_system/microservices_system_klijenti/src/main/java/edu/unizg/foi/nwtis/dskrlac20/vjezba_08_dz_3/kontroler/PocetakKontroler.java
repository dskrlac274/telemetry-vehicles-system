package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.kontroler;

import jakarta.enterprise.context.RequestScoped;
import jakarta.mvc.Controller;
import jakarta.mvc.View;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Controller
@Path("pocetak")
@RequestScoped
public class PocetakKontroler {
  @Context
  private HttpServletRequest zahtjev;

  /**
   * Pocetak.
   */
  @GET
  @View("index.jsp")
  public void pocetak() {
  }

  /**
   * Odjava korisnika.
   */
  @GET
  @Path("/odjava")
  public String odjava() {
    HttpSession sesija = zahtjev.getSession(false);

    if (sesija != null) {
      try {
        sesija.invalidate();
        zahtjev.logout();
      } catch (ServletException e) {
        return "redirect:" + "/pocetak";
      }
    }

    return "redirect:" + "/pocetak";
  }
}
