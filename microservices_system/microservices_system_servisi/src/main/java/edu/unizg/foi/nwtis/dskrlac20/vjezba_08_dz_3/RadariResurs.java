package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Radar;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;

/**
 * Klasa {@code RadariResurs} je RESTful web servis za upravljanje radarima.
 * Omogućuje dohvat, provjeru, resetiranje i brisanje radara.
 *
 * @author dskrlac20
 */
@Path("nwtis/v1/api/radari")
public class RadariResurs {
	@Context
	private ServletContext kontekst;
	private Integer mreznaVrataPosluziteljaZaRegistraciju;
	private String adresaPosluziteljaZaRegistraciju;

  /**
	 * Inicijalizira resurs za rad s radarima.
	 */
	@PostConstruct
	private void pripremiResurseRadara() {
		System.out.println("Pokrećem REST: " + this.getClass().getName());
		try {
			preuzmiPostavke();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Dohvaća sve radare.
	 *
	 * @return odgovor s listom radara
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dohvatiSveRadare() {
		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaRegistraciju,
				mreznaVrataPosluziteljaZaRegistraciju, "RADAR SVI");
		return odgovor == null
				? Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Poslužitelj za registraciju radara nije aktivan.").build()
				: Response.status(Response.Status.OK).entity(parsirajRadare(odgovor)).build();
	}

	/**
	 * Dohvaća radar prema id-u.
	 *
	 * @param id id radara
	 * @return odgovor s radarom ili porukom o grešci
	 */
	@Path("{id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dohvatiRadar(@PathParam("id") int id) {
		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaRegistraciju,
				mreznaVrataPosluziteljaZaRegistraciju, "RADAR SVI");
		if (odgovor == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Poslužitelj za registraciju radara nije aktivan.").build();
		}

		var radar = parsirajRadare(odgovor).stream().filter(r -> r.getId() == id).findFirst()
				.orElse(null);

		return radar != null
				? Response.status(Response.Status.OK).entity(radar).build()
				: Response.status(Response.Status.NOT_FOUND).entity("Radar " + id + " ne postoji.").build();
	}

	/**
	 * Resetira sve radare.
	 *
	 * @return odgovor s informacijom o rezultatu resetiranja
	 */
	@Path("/reset")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response resetirajRadare() {
		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaRegistraciju,
				mreznaVrataPosluziteljaZaRegistraciju, "RADAR RESET");
		return odgovor == null
				? Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Poslužitelj za registraciju radara nije aktivan.").build()
				: Response.status(Response.Status.OK).entity("Radari uspješno resetirani - " + odgovor + ".").build();
	}

	/**
	 * Provjerava status radara prema id-u.
	 *
	 * @param id id radara
	 * @return odgovor s informacijom o statusu radara
	 */
	@Path("/{id}/provjeri")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response provjeriRadar(@PathParam("id") int id) {
		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaRegistraciju,
				mreznaVrataPosluziteljaZaRegistraciju, "RADAR " + id);
		if (odgovor == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Poslužitelj za registraciju radara nije aktivan.").build();
		}

		return odgovor.contains("OK")
				? Response.status(Response.Status.OK).entity("Radar " + id + " postoji.").build()
				: Response.status(Response.Status.OK).entity(odgovor).build();
	}

	/**
	 * Briše sve radare.
	 *
	 * @return odgovor s informacijom o rezultatu brisanja
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response obrisiRadar() {
		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaRegistraciju,
				mreznaVrataPosluziteljaZaRegistraciju, "RADAR OBRIŠI SVE");
		return odgovor == null
				? Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Poslužitelj za registraciju radara nije aktivan.").build()
				: Response.status(Response.Status.OK).entity(odgovor.contains("OK") ? "Svi radari su uspješno obrisani." : odgovor).build();
	}

	/**
	 * Briše radar prema id-u.
	 *
	 * @param id id radara
	 * @return odgovor s informacijom o rezultatu brisanja
	 */
	@Path("{id}")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response obrisiRadar(@PathParam("id") int id) {
		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaRegistraciju,
				mreznaVrataPosluziteljaZaRegistraciju, "RADAR OBRIŠI " + id);
		return odgovor == null
				? Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Poslužitelj za registraciju radara nije aktivan.").build()
				: Response.status(Response.Status.OK).entity(odgovor.contains("OK") ? "Radar " + id + " uspješno obrisan." : odgovor).build();
	}

	/**
	 * Parsira podatke o radaru iz stringa.
	 *
	 * @param radar string s podacima o radaru
	 * @return objekt {@code Radar} ili null ako nije moguće parsirati podatke
	 */
	private Radar parsirajRadar(String radar) {
		try {
			String[] podaciRadara = radar.split("\\s+");

			return new Radar(Integer.parseInt(podaciRadara[0]), podaciRadara[1],
					Integer.parseInt(podaciRadara[2]), Double.parseDouble(podaciRadara[3]),
					Double.parseDouble(podaciRadara[4]), Integer.parseInt(podaciRadara[5]));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Parsira podatke o više radara iz stringa.
	 *
	 * @param radari string s podacima o radarima
	 * @return lista objekata {@code Radar}
	 */
	private List<Radar> parsirajRadare(String radari) {
		String cistiRadari = radari.replaceAll("^OK\\s*", "").replaceAll("^\\{\\[|]}$", "");

		if (cistiRadari.isEmpty() || cistiRadari.equals("{}")) {
			return new ArrayList<>();
		}

		String[] podaciRadara = cistiRadari.split("],\\s*\\[");

		List<Radar> radariPolje = new ArrayList<>();

		for (String podaci : podaciRadara) {
			Radar radar = parsirajRadar(podaci);
			if (radar != null) {
				radariPolje.add(radar);
			}
		}

		return radariPolje;
	}

	/**
	 * Preuzima postavke iz konfiguracijske datoteke.
	 *
	 * @throws RuntimeException ako dođe do greške prilikom preuzimanja postavki
	 */
	private void preuzmiPostavke() throws Exception {
		mreznaVrataPosluziteljaZaRegistraciju = Integer.parseInt(
            (String) kontekst.getAttribute("app.radari.mreznaVrata"));
		adresaPosluziteljaZaRegistraciju = (String) kontekst.getAttribute("app.radari.adresa");
	}
}
