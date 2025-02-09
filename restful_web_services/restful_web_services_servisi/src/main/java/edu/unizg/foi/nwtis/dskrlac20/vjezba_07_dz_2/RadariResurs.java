package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Radar;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.*;
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
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "PosluziteljZaRegistracijuRadara nije aktivan.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		restOdgovor.put("odgovor", parsirajRadare(odgovor));
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
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
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "PosluziteljZaRegistracijuRadara nije aktivan.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		var radar = parsirajRadare(odgovor).stream().filter(r -> r.getId() == id).findFirst()
				.orElse(null);

		if (radar != null) {
			restOdgovor.put("odgovor", radar);
			return Response.status(Response.Status.OK).entity(restOdgovor).build();
		} else {
			restOdgovor.put("odgovor", "Radar " + id + " ne postoji.");
			return Response.status(Response.Status.NOT_FOUND).entity(restOdgovor).build();
		}
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
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "PosluziteljZaRegistracijuRadara nije aktivan.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		restOdgovor.put("odgovor", "Radari uspjesno resetirani - " + odgovor + ".");
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
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
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "PosluziteljZaRegistracijuRadara nije aktivan.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		if (odgovor.contains("OK")) {
			restOdgovor.put("odgovor", "Radar " + id + " postoji.");
		} else {
			restOdgovor.put("odgovor", odgovor);
		}
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
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
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "PosluziteljZaRegistracijuRadara nije aktivan.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		if (odgovor.contains("OK")) {
			restOdgovor.put("odgovor", "Svi radari su uspjesno obrisani.");
		} else {
			restOdgovor.put("odgovor", odgovor);
		}
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
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
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "PosluziteljZaRegistracijuRadara nije aktivan.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		if (odgovor.contains("OK")) {
			restOdgovor.put("odgovor", "Radar " + id + " uspjesno obrisan.");
		} else {
			restOdgovor.put("odgovor", odgovor);
		}
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
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
	 * @throws Exception ako dođe do greške prilikom preuzimanja postavki
	 */
	private void preuzmiPostavke() throws Exception {
		String datotekaKonfiguracije = "NWTiS_REST_R.txt";
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfiguracije);

		mreznaVrataPosluziteljaZaRegistraciju = Integer
				.parseInt(konfig.dajPostavku("mreznaVrataRegistracije"));
		adresaPosluziteljaZaRegistraciju = konfig.dajPostavku("adresaRegistracije");
	}
}
