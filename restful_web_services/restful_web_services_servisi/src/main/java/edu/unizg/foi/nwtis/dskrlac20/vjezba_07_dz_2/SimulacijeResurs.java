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
 * Klasa {@code SimulacijeResurs} je RESTful web servis za upravljanje
 * simulacijama vožnji vozila. Omogućuje dohvat, dodavanje i upravljanje
 * vožnjama vozila.
 *
 * @author dskrlac20
 */
@Path("nwtis/v1/api/simulacije")
public class SimulacijeResurs extends SviResursi {
	private VozilaDAO vozilaDAO = null;
	private Integer mreznaVrataPosluziteljaZaVozila;
	private String adresaPosluziteljaZaVozila;
	private final String datotekaKonfiguracije = "NWTiS_REST_S.txt";

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
			this.vozilaDAO = new VozilaDAO(vezaBP, "voznje");
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
	 * @param id        ID vozila
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
			restOdgovor.put("odgovor", vozilaDAO.dohvatiVoznjeVozila(id, "voznje").toArray());
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
	public Response dodajVoznju(Vozila novoVozilo) {
		var odgovor = vozilaDAO.dodajVoznju(novoVozilo);
		Map<String, Object> restOdgovor = new HashMap<>();
		if (odgovor == null) {
			restOdgovor.put("odgovor", "Neuspješni upis voznje u bazu podataka.");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(restOdgovor)
					.build();
		}

		MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaVozila,
				mreznaVrataPosluziteljaZaVozila, kreirajKomanduVozila(novoVozilo));

		restOdgovor.put("odgovor", "Voznja uspjesno dodana.");
		return Response.status(Response.Status.OK).entity(restOdgovor).build();
	}

	/**
	 * Kreira komandu vozila za slanje poslužitelju.
	 *
	 * @param vozilo objekt vozila
	 * @return string s komandom vozila
	 */
	private String kreirajKomanduVozila(Vozila vozilo) {
		StringBuilder sb = new StringBuilder();
		sb.append("VOZILO ").append(vozilo.getId()).append(" ").append(vozilo.getBroj()).append(" ")
				.append(vozilo.getVrijeme()).append(" ").append(vozilo.getBrzina()).append(" ")
				.append(vozilo.getSnaga()).append(" ").append(vozilo.getStruja()).append(" ")
				.append(vozilo.getVisina()).append(" ").append(vozilo.getGpsBrzina()).append(" ")
				.append(vozilo.getTempVozila()).append(" ").append(vozilo.getPostotakBaterija())
				.append(" ").append(vozilo.getNaponBaterija()).append(" ")
				.append(vozilo.getKapacitetBaterija()).append(" ").append(vozilo.getTempBaterija())
				.append(" ").append(vozilo.getPreostaloKm()).append(" ")
				.append(vozilo.getUkupnoKm()).append(" ").append(vozilo.getGpsSirina()).append(" ")
				.append(vozilo.getGpsDuzina());
		return sb.toString();
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
