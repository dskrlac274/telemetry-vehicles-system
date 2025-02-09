package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Pracenevoznje;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.pomocnici.PraceneVoznjeFacade;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.pomocnici.VozilaFacade;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Vozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Klasa {@code VozilaResurs} je RESTful web servis za upravljanje vožnjama
 * vozila. Omogućuje dohvat, praćenje, zaustavljanje praćenja i dodavanje vožnji
 * vozila.
 *
 * @author dskrlac20
 */
@Path("nwtis/v1/api/vozila")
public class VozilaResurs {
	@Context
	private ServletContext kontekst;
	@Inject
	PraceneVoznjeFacade praceneVoznjeFacade;
	@Inject
	VozilaFacade vozilaFacade;
	private Integer mreznaVrataPosluziteljaZaVozila;
	private String adresaPosluziteljaZaVozila;

  	/**
	 * Inicijalizira resurs za rad s vožnjama vozila.
	 */
	@PostConstruct
	private void pripremiVozila() {
		System.out.println("Pokrećem REST: " + this.getClass().getName());
		try {
			preuzmiPostavke();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Dohvaća sve vožnje ili vožnje u zadanom vremenskom rasponu ako su zadani parametri.
	 *
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista vožnji ili prazna lista ako nema rezultata
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response dohvatiSveVoznje(@QueryParam("od") Long odVremena,
			@QueryParam("do") Long doVremena) {
		return Response.status(Response.Status.OK).entity(odVremena != null && doVremena != null ?
				pretvoriKolekcijuPracenevoznje(praceneVoznjeFacade.dohvatiVoznjeURasponu(odVremena, doVremena)) :
				Collections.emptyList()).build();
	}

	/**
	 * Dohvaća sve vožnje određenog vozila prema id-u, opcionalno u zadanom vremenskom rasponu.
	 *
	 * @param id        id vozila
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista vožnji ili prazna lista ako nema rezultata
	 */
	@Path("/vozilo/{id}")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response dohvatiSveVoznjeVozila(@PathParam("id") int id, @QueryParam("od") Long odVremena,
			@QueryParam("do") Long doVremena) {
		var voziloEntitet = vozilaFacade.find(id);

		if (voziloEntitet == null) {
			return Response.status(Response.Status.OK).entity("Vozilo ne postoji.").build();
		}

		return Response.status(Response.Status.OK).entity(odVremena != null && doVremena != null ?
				pretvoriKolekcijuPracenevoznje(
						praceneVoznjeFacade.dohvatiVoznjeVozilaURasponu(id, odVremena, doVremena)) :
				pretvoriKolekcijuPracenevoznje(praceneVoznjeFacade.dohvatiVoznjeVozila(id))).build();
	}

	/**
	 * Pokreće praćenje vožnje određenog vozila prema id-u.
	 *
	 * @param id id vozila
	 * @return informacija o rezultatu pokretanja praćenja
	 */
	@Path("/vozilo/{id}/start")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response pokreniPracenjeVoznje(@PathParam("id") int id) {
		var voziloEntitet = vozilaFacade.find(id);

		if (voziloEntitet == null) {
			return Response.status(Response.Status.OK).entity("Vozilo ne postoji.").build();
		}

		String odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaVozila,
				mreznaVrataPosluziteljaZaVozila, "VOZILO START " + id);

		return Response.status(Response.Status.OK).entity(odgovor != null && odgovor.contains("OK") ?
				"Pracenje vozila " + id + " je zapocelo." :
				odgovor != null ? odgovor : "PosluziteljZaVozila nije aktivan.").build();
	}

	/**
	 * Zaustavlja praćenje vožnje određenog vozila prema id-u.
	 *
	 * @param id id vozila
	 * @return informacija o rezultatu zaustavljanja praćenja
	 */
	@Path("/vozilo/{id}/stop")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response zaustaviPracenjeVoznje(@PathParam("id") int id) {
		var voziloEntitet = vozilaFacade.find(id);

		if (voziloEntitet == null) {
			return Response.status(Response.Status.OK).entity("Vozilo ne postoji.").build();
		}

		String odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaVozila,
				mreznaVrataPosluziteljaZaVozila, "VOZILO STOP " + id);

		return Response.status(Response.Status.OK).entity(odgovor != null && odgovor.contains("OK") ?
				"Pracenje vozila " + id + " je zaustavljeno." :
				odgovor != null ? odgovor : "PosluziteljZaVozila nije aktivan.").build();
	}

	/**
	 * Dodaje novu praćenu vožnju.
	 *
	 * @param novoVozilo objekt nove vožnje
	 * @return informacija o rezultatu dodavanja vožnje
	 */
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response dodajPracenuVoznju(Vozila novoVozilo) {
		var voznja = pretvoriVozila(novoVozilo);
		if (voznja == null) {
			return Response.status(Response.Status.OK).entity("Vozilo ne postoji.").build();
		}

		var odgovor = praceneVoznjeFacade.dodajVoznju(voznja);
		return Response.status(odgovor ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR)
				.entity(odgovor ?
						"Pracena voznja uspjesno dodana." :
						"Neuspješni upis pracene voznje u bazu podataka.").build();
	}

	/**
	 * Preuzima postavke iz konfiguracijske datoteke.
	 *
	 * @throws RuntimeException ako dođe do greške prilikom preuzimanja postavki
	 */
	private void preuzmiPostavke() throws Exception {
		mreznaVrataPosluziteljaZaVozila = Integer.parseInt( (String)  kontekst.getAttribute("app.vozila.mreznaVrata"));
		adresaPosluziteljaZaVozila = (String) kontekst.getAttribute("app.vozila.adresa");
	}

	/**
	 * Pretvara objekt tipa Vozila u objekt tipa Pracenevoznje.
	 *
	 * @param vozilo objekt tipa Vozila koji se pretvara
	 * @return objekt tipa Pracenevoznje ili null ako je vozilo null
	 */
	private Pracenevoznje pretvoriVozila(Vozila vozilo) {
		if (vozilo == null) {
			return null;
		}

		Pracenevoznje pracenevoznje = new Pracenevoznje();
		pracenevoznje.setBroj(vozilo.getBroj());
		pracenevoznje.setVrijeme(vozilo.getVrijeme());
		pracenevoznje.setBrzina(vozilo.getBrzina());
		pracenevoznje.setSnaga(vozilo.getSnaga());
		pracenevoznje.setStruja(vozilo.getStruja());
		pracenevoznje.setVisina(vozilo.getVisina());
		pracenevoznje.setGpsbrzina(vozilo.getGpsBrzina());
		pracenevoznje.setTempvozila(vozilo.getTempVozila());
		pracenevoznje.setPostotakbaterija(vozilo.getPostotakBaterija());
		pracenevoznje.setNaponbaterija(vozilo.getNaponBaterija());
		pracenevoznje.setKapacitetbaterija(vozilo.getKapacitetBaterija());
		pracenevoznje.setTempbaterija(vozilo.getTempBaterija());
		pracenevoznje.setPreostalokm(vozilo.getPreostaloKm());
		pracenevoznje.setUkupnokm(vozilo.getUkupnoKm());
		pracenevoznje.setGpssirina(vozilo.getGpsSirina());
		pracenevoznje.setGpsduzina(vozilo.getGpsDuzina());

		var voziloEntitet = vozilaFacade.find(vozilo.getId());

		if (voziloEntitet == null)
			return null;

		pracenevoznje.setId(voziloEntitet);

		return pracenevoznje;
	}

	/**
	 * Pretvara objekt tipa Pracenevoznje u objekt tipa Vozila.
	 *
	 * @param pracenevoznje objekt tipa Pracenevoznje koji se pretvara
	 * @return objekt tipa Vozila ili null ako je pracenevoznje null
	 */
	private Vozila pretvoriPracenevoznje(Pracenevoznje pracenevoznje) {
		if (pracenevoznje == null) {
			return null;
		}

		return new Vozila(pracenevoznje.getId().getVozilo(), pracenevoznje.getBroj(), pracenevoznje.getVrijeme(),
				pracenevoznje.getBrzina(), pracenevoznje.getSnaga(), pracenevoznje.getStruja(),
				pracenevoznje.getVisina(), pracenevoznje.getGpsbrzina(), pracenevoznje.getTempvozila(),
				pracenevoznje.getPostotakbaterija(), pracenevoznje.getNaponbaterija(),
				pracenevoznje.getKapacitetbaterija(), pracenevoznje.getTempbaterija(),
				pracenevoznje.getPreostalokm(), pracenevoznje.getUkupnokm(), pracenevoznje.getGpssirina(),
				pracenevoznje.getGpsduzina());
	}

	/**
	 * Pretvara kolekciju objekata tipa Pracenevoznje u kolekciju objekata tipa Vozila.
	 *
	 * @param pracenevoznje popis objekata tipa Pracenevoznje koji se pretvaraju
	 * @return kolekcija objekata tipa Vozila
	 */
	private List<Vozila> pretvoriKolekcijuPracenevoznje(List<Pracenevoznje> pracenevoznje) {
		var kaznaKolekcija = new ArrayList<Vozila>();
		for (Pracenevoznje k : pracenevoznje) {
			var kazna = pretvoriPracenevoznje(k);
			kaznaKolekcija.add(kazna);
		}
		return kaznaKolekcija;
	}
}
