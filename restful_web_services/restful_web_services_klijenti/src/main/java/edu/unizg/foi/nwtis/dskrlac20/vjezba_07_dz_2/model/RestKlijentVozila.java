package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.model;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Vozila;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Klasa {@code RestKlijentVozila} omogućuje komunikaciju s RESTful web servisom
 * za upravljanje vožnjama vozila.
 *
 * @author dskrlac20
 */
public class RestKlijentVozila {
	private final WebTarget webPredlozak;

	/**
	 * Konstruktor koji inicijalizira REST klijenta s baznom URL adresom i zadanom
	 * putanjom.
	 *
	 * @param vozila putanja za resurs vozila
	 */
	public RestKlijentVozila(String vozila) {
		String BASE_URI = "http://localhost:9080/";
		Client klijent = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS)
				.readTimeout(5, TimeUnit.SECONDS).build();
		webPredlozak = klijent.target(BASE_URI).path("nwtis/v1/api/" + vozila);
	}

	/**
	 * Dohvaća vožnje u zadanom vremenskom rasponu.
	 *
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista vožnji
	 */
	public List<Vozila> dohvatiVozilaURasponu(long odVremena, long doVremena) {
		WebTarget predlozak = webPredlozak.queryParam("od", odVremena).queryParam("do", doVremena);
		Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

		List<Vozila> vozila = new ArrayList<>();

		try {
			Response restOdgovor = zahtjev.get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				try (Jsonb jb = JsonbBuilder.create()) {
					var jsonResponse = jb.fromJson(odgovor, Map.class);
					Vozila[] pVozila = jb.fromJson(jb.toJson(jsonResponse.get("odgovor")),
							Vozila[].class);
					vozila.addAll(Arrays.asList(pVozila));
				}
			}
		} catch (Exception e) {
			return null;
		}

		return vozila;
	}

	/**
	 * Dohvaća vožnje određenog vozila prema id-u.
	 *
	 * @param id id vozila
	 * @return lista vožnji
	 */
	public List<Vozila> dohvatiVoznjeVozila(String id) {
		WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("vozilo/{0}", id));
		Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

		List<Vozila> vozila = new ArrayList<>();

		try {
			Response restOdgovor = zahtjev.get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				try (Jsonb jb = JsonbBuilder.create()) {
					var jsonResponse = jb.fromJson(odgovor, Map.class);
					Vozila[] pVozila = jb.fromJson(jb.toJson(jsonResponse.get("odgovor")),
							Vozila[].class);
					vozila.addAll(Arrays.asList(pVozila));
				}
			}
		} catch (Exception e) {
			return null;
		}

		return vozila;
	}

	/**
	 * Dohvaća vožnje određenog vozila u zadanom vremenskom rasponu.
	 *
	 * @param id        id vozila
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista vožnji
	 */
	public List<Vozila> dohvatiVoznjeVozilaURasponu(String id, long odVremena, long doVremena) {
		WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("vozilo/{0}", id))
				.queryParam("od", odVremena).queryParam("do", doVremena);
		Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

		List<Vozila> vozila = new ArrayList<>();

		try {
			Response restOdgovor = zahtjev.get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				try (Jsonb jb = JsonbBuilder.create()) {
					var jsonResponse = jb.fromJson(odgovor, Map.class);
					Vozila[] pVozila = jb.fromJson(jb.toJson(jsonResponse.get("odgovor")),
							Vozila[].class);
					vozila.addAll(Arrays.asList(pVozila));
				}
			}
		} catch (Exception e) {
			return null;
		}

		return vozila;
	}

	/**
	 * Pokreće praćenje vožnje određenog vozila.
	 *
	 * @param id id vozila
	 * @return string s informacijom o rezultatu pokretanja praćenja
	 */
	public String pokreniPracenje(String id) {
		WebTarget predlozak = webPredlozak
				.path(java.text.MessageFormat.format("vozilo/{0}/start", id));
		Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

		try (Response restOdgovor = zahtjev.get()) {
			String odgovor = restOdgovor.readEntity(String.class);
			try (Jsonb jb = JsonbBuilder.create()) {
				var jsonResponse = jb.fromJson(odgovor, Map.class);
				return (String) jsonResponse.get("odgovor");
			}
		} catch (Exception ignored) {
			return "REST servis nije dostupan.";
		}
	}

	/**
	 * Zaustavlja praćenje vožnje određenog vozila.
	 *
	 * @param id id vozila
	 * @return string s informacijom o rezultatu zaustavljanja praćenja
	 */
	public String zaustaviPracenje(String id) {
		WebTarget predlozak = webPredlozak
				.path(java.text.MessageFormat.format("vozilo/{0}/stop", id));
		Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

		try (Response restOdgovor = zahtjev.get()) {
			String odgovor = restOdgovor.readEntity(String.class);
			try (Jsonb jb = JsonbBuilder.create()) {
				var jsonResponse = jb.fromJson(odgovor, Map.class);
				return (String) jsonResponse.get("odgovor");
			} catch (Exception e) {
				return "REST servis nije dostupan.";
			}
		} catch (Exception ignored) {
			return "REST servis nije dostupan.";
		}
	}

	/**
	 * Dodaje novu vožnju vozila putem REST servisa.
	 *
	 * @param vozilo objekt vožnje vozila
	 * @return string s informacijom o rezultatu dodavanja vožnje
	 */
	public String dodajVoznjuVozila(Vozila vozilo) {
		Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);

		try (Response restOdgovor = zahtjev
				.post(Entity.entity(vozilo, MediaType.APPLICATION_JSON))) {
			String odgovor = restOdgovor.readEntity(String.class);
			try (Jsonb jb = JsonbBuilder.create()) {
				var jsonResponse = jb.fromJson(odgovor, Map.class);
				return (String) jsonResponse.get("odgovor");
			} catch (Exception e) {
				return "REST servis nije dostupan.";
			}
		} catch (Exception ignored) {
			return "REST servis nije dostupan.";
		}
	}
}
