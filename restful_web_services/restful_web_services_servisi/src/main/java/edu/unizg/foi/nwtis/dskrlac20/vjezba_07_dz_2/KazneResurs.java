package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Kazna;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.KaznaDAO;
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
 * Klasa {@code KazneResurs} je resurs RESTful web servisa za upravljanje
 * kaznama. Omogućuje dohvat, dodavanje i provjeru stanja kazni.
 *
 * @author dskrlac20
 */
@Path("nwtis/v1/api/kazne")
public class KazneResurs extends SviResursi {
	private KaznaDAO kaznaDAO = null;
	private Integer mreznaVrataPosluziteljaKazni;
	private String adresaPosluziteljaKazni;
	private final String datotekaKonfiguracije = "NWTiS_REST_K.txt";

	/**
	 * Inicijalizira resurs za rad s kaznama.
	 */
	@PostConstruct
	private void pripremiKazneDAO() {
		System.out.println("Pokrećem REST: " + this.getClass().getName());
		try {
			preuzmiPostavke();
			this.vezaBazaPodataka.inicijaliziraj(datotekaKonfiguracije);
			var vezaBP = this.vezaBazaPodataka.getVezaBazaPodataka();
			this.kaznaDAO = new KaznaDAO(vezaBP);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Dohvaća sve kazne ili kazne u zadanom vremenskom rasponu ako su zadani
	 * parametri.
	 *
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return odgovor s listom kazni
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dohvatiSveKazne(@QueryParam("od") Long odVremena,
			@QueryParam("do") Long doVremena) {
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odVremena != null && doVremena != null) {
			restOdgovor.put("odgovor",
					kaznaDAO.dohvatiKazneURasponu(odVremena, doVremena).toArray());
		} else {
			restOdgovor.put("odgovor", kaznaDAO.dohvatiSveKazne().toArray());
		}
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Dohvaća kaznu prema rednom broju.
	 *
	 * @param rb redni broj kazne
	 * @return odgovor s kaznom ili porukom o grešci
	 */
	@Path("{rb}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dohvatiKaznu(@PathParam("rb") int rb) {
		var kazna = kaznaDAO.dohvatiKaznu(rb);
		Map<String, Object> restOdgovor = new HashMap<>();
		if (kazna == null) {
			restOdgovor.put("odgovor", "Navedena kazna ne postoji.");
			return Response.status(Response.Status.NOT_FOUND).entity(restOdgovor).build();
		}
		restOdgovor.put("odgovor", kazna);
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Dohvaća kazne za određeno vozilo prema id-u, opcionalno u zadanom vremenskom
	 * rasponu.
	 *
	 * @param id        ID vozila
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return odgovor s listom kazni
	 */
	@Path("/vozilo/{id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dohvatiKazneZaVozilo(@PathParam("id") int id, @QueryParam("od") Long odVremena,
			@QueryParam("do") Long doVremena) {
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odVremena != null && doVremena != null) {
			restOdgovor.put("odgovor",
					kaznaDAO.dohvatiKazneVozilaURasponu(id, odVremena, doVremena));
		} else {
			restOdgovor.put("odgovor", kaznaDAO.dohvatiKazneVozila(id));
		}
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Provjerava stanje poslužitelja kazni.
	 *
	 * @return odgovor s informacijom o stanju poslužitelja
	 */
	@HEAD
	@Produces({ MediaType.APPLICATION_JSON })
	public Response provjeriStanjePosluziteljaKazni() {
		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaKazni,
				mreznaVrataPosluziteljaKazni, "TEST");

		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor != null) {
			restOdgovor.put("odgovor", "PosluziteljKazni je aktivan.");
			return Response.status(Response.Status.OK).entity(restOdgovor).build();
		}
		restOdgovor.put("odgovor", "PosluziteljKazni nije aktivan.");
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor).build();
	}

	/**
	 * Dodaje novu kaznu.
	 *
	 * @param novaKazna objekt nove kazne
	 * @return odgovor s informacijom o rezultatu dodavanja kazne
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dodajKaznu(Kazna novaKazna) {
		var odgovor = kaznaDAO.dodajKaznu(novaKazna);
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "Neuspjesni upis kazne u bazu podataka.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}
		restOdgovor.put("odgovor", "Kazna uspjesno dodana.");
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Preuzima postavke iz konfiguracijske datoteke.
	 *
	 * @throws Exception ako dođe do greške prilikom preuzimanja postavki
	 */
	private void preuzmiPostavke() throws Exception {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfiguracije);
		mreznaVrataPosluziteljaKazni = Integer.parseInt(konfig.dajPostavku("mreznaVrataKazne"));
		adresaPosluziteljaKazni = konfig.dajPostavku("adresaKazne");
	}
}
