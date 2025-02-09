package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Vozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.VozilaDAO;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasa {@code VozilaResurs} je RESTful web servis za upravljanje vožnjama
 * vozila. Omogućuje dohvat, praćenje, zaustavljanje praćenja i dodavanje vožnji
 * vozila.
 *
 * @author dskrlac20
 */
@Path("nwtis/v1/api/vozila")
public class VozilaResurs extends SviResursi {
	private VozilaDAO vozilaDAO = null;
	private Integer mreznaVrataPosluziteljaZaVozila;
	private String adresaPosluziteljaZaVozila;
	private final String datotekaKonfiguracije = "NWTiS_REST_V.txt";

	/**
	 * Inicijalizira resurs za rad s vožnjama vozila.
	 */
	@PostConstruct
	private void pripremiVozilaDAO() {
		System.out.println("Pokrećem REST: " + this.getClass().getName());
		try {
			preuzmiPostavke();
			this.vezaBazaPodataka.inicijaliziraj(datotekaKonfiguracije);
			var vezaBP = this.vezaBazaPodataka.getVezaBazaPodataka();
			this.vozilaDAO = new VozilaDAO(vezaBP, "pracenevoznje");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Dohvaća sve vožnje ili vožnje u zadanom vremenskom rasponu ako su zadani
	 * parametri.
	 *
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return odgovor s listom vožnji
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dohvatiSveVoznje(@QueryParam("od") Long odVremena,
			@QueryParam("do") Long doVremena) {
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odVremena != null && doVremena != null) {
			restOdgovor.put("odgovor",
					vozilaDAO.dohvatiVoznjeURasponu(odVremena, doVremena).toArray());
		}

		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Dohvaća sve vožnje određenog vozila prema id-u, opcionalno u zadanom
	 * vremenskom rasponu.
	 *
	 * @param id        id vozila
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return odgovor s listom vožnji
	 */
	@Path("/vozilo/{id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dohvatiSveVoznjeVozila(@PathParam("id") int id,
			@QueryParam("od") Long odVremena, @QueryParam("do") Long doVremena) {
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odVremena != null && doVremena != null) {
			restOdgovor.put("odgovor",
					vozilaDAO.dohvatiVoznjeVozilaURasponu(id, odVremena, doVremena).toArray());
		} else {
			restOdgovor.put("odgovor",
					vozilaDAO.dohvatiVoznjeVozila(id, "pracenevoznje").toArray());
		}
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Pokreće praćenje vožnje određenog vozila prema id-u.
	 *
	 * @param id id vozila
	 * @return odgovor s informacijom o rezultatu pokretanja praćenja
	 */
	@Path("/vozilo/{id}/start")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response pokreniPracenjeVoznje(@PathParam("id") int id) {
		String odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaVozila,
				mreznaVrataPosluziteljaZaVozila, "VOZILO START " + id);

		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "PosluziteljZaVozila nije aktivan.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		if (odgovor.contains("OK")) {
			restOdgovor.put("odgovor", "Pracenje vozila " + id + " je zapocelo.");
		} else {
			restOdgovor.put("odgovor", odgovor);
		}
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Zaustavlja praćenje vožnje određenog vozila prema id-u.
	 *
	 * @param id id vozila
	 * @return odgovor s informacijom o rezultatu zaustavljanja praćenja
	 */
	@Path("/vozilo/{id}/stop")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response zaustaviPracenjeVoznje(@PathParam("id") int id) {
		String odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaVozila,
				mreznaVrataPosluziteljaZaVozila, "VOZILO STOP " + id);

		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "PosluziteljZaVozila nije aktivan.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		if (odgovor.contains("OK")) {
			restOdgovor.put("odgovor", "Pracenje vozila " + id + " je zaustavljeno.");
		} else {
			restOdgovor.put("odgovor", odgovor);
		}
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Dodaje novu praćenu vožnju.
	 *
	 * @param novoVozilo objekt nove vožnje
	 * @return odgovor s informacijom o rezultatu dodavanja vožnje
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dodajPracenuVoznju(Vozila novoVozilo) {
		var odgovor = vozilaDAO.dodajVoznju(novoVozilo);
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "Neuspješni upis pracene voznje u bazu podataka.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		restOdgovor.put("odgovor", "Pracena voznja uspjesno dodana.");
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Preuzima postavke iz konfiguracijske datoteke.
	 *
	 * @throws Exception ako dođe do greške prilikom preuzimanja postavki
	 */
	private void preuzmiPostavke() throws Exception {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfiguracije);

		mreznaVrataPosluziteljaZaVozila = Integer.parseInt(konfig.dajPostavku("mreznaVrataVozila"));
		adresaPosluziteljaZaVozila = konfig.dajPostavku("adresaVozila");
	}
}
