package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.kontroler;

import jakarta.enterprise.context.RequestScoped;
import jakarta.mvc.Controller;
import jakarta.mvc.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * Klasa {@code PocetakKontroler} je MVC kontroler koji upravlja početnom
 * stranicom aplikacije.
 *
 * @author dskrlac20
 */
@Controller
@Path("pocetak")
@RequestScoped
public class PocetakKontroler {

	/**
	 * Prikazuje početnu stranicu aplikacije.
	 */
	@GET
	@View("index.jsp")
	public void pocetak() {
	}
}
