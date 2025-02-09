package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.model;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Kazna;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
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
 * Klasa {@code RestKlijentKazne} omogućuje komunikaciju s RESTful web servisom
 * za upravljanje kaznama.
 *
 * @author dskrlac20
 */
public class RestKlijentKazne {
	private final WebTarget webPredlozak;

	/**
	 * Konstruktor koji inicijalizira REST klijenta s baznom URL adresom.
	 */
	public RestKlijentKazne() {
		String BASE_URI = "http://localhost:9080/";
		Client klijent = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS)
				.readTimeout(5, TimeUnit.SECONDS).build();
		webPredlozak = klijent.target(BASE_URI).path("nwtis/v1/api/kazne");
	}

	/**
	 * Dohvaća sve kazne iz REST servisa.
	 *
	 * @return lista kazni
	 */
	public List<Kazna> dohvatiKazne() {
		Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);

		List<Kazna> kazne = new ArrayList<>();

		try {
			Response restOdgovor = zahtjev.get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				try (Jsonb jb = JsonbBuilder.create()) {
					var jsonResponse = jb.fromJson(odgovor, Map.class);
					Kazna[] pkazne = jb.fromJson(jb.toJson(jsonResponse.get("odgovor")),
							Kazna[].class);
					kazne.addAll(Arrays.asList(pkazne));
				}
			}
		} catch (Exception e) {
			return null;
		}

		return kazne;
	}

	/**
	 * Dohvaća određenu kaznu prema rednom broju.
	 *
	 * @param rb redni broj kazne
	 * @return kazna
	 */
	public Kazna dohvatiKaznu(String rb) {
		WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("{0}", rb));
		Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

		try {
			Response restOdgovor = zahtjev.get();
			String odgovor = restOdgovor.readEntity(String.class);
			try (Jsonb jb = JsonbBuilder.create()) {
				var jsonResponse = jb.fromJson(odgovor, Map.class);
				return jb.fromJson(jb.toJson(jsonResponse.get("odgovor")), Kazna.class);
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Dohvaća kazne u zadanom vremenskom rasponu.
	 *
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista kazni
	 */
	public List<Kazna> dohvatiKazneURasponu(long odVremena, long doVremena) {
		WebTarget predlozak = webPredlozak.queryParam("od", odVremena).queryParam("do", doVremena);
		Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

		List<Kazna> kazne = new ArrayList<>();

		try {
			Response restOdgovor = zahtjev.get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				try (Jsonb jb = JsonbBuilder.create()) {
					var jsonResponse = jb.fromJson(odgovor, Map.class);
					Kazna[] pkazne = jb.fromJson(jb.toJson(jsonResponse.get("odgovor")),
							Kazna[].class);
					kazne.addAll(Arrays.asList(pkazne));
				}
			}
		} catch (Exception e) {
			return null;
		}

		return kazne;
	}

	/**
	 * Dohvaća kazne za određeno vozilo prema id-u.
	 *
	 * @param id id vozila
	 * @return lista kazni
	 */
	public List<Kazna> dohvatiKazneVozila(String id) {
		WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("vozilo/{0}", id));
		Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

		List<Kazna> kazne = new ArrayList<>();

		try {
			Response restOdgovor = zahtjev.get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				try (Jsonb jb = JsonbBuilder.create()) {
					var jsonResponse = jb.fromJson(odgovor, Map.class);
					Kazna[] pkazne = jb.fromJson(jb.toJson(jsonResponse.get("odgovor")),
							Kazna[].class);
					kazne.addAll(Arrays.asList(pkazne));
				}
			}
		} catch (Exception e) {
			return null;
		}

		return kazne;
	}

	/**
	 * Dohvaća kazne za određeno vozilo u zadanom vremenskom rasponu.
	 *
	 * @param id        id vozila
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista kazni
	 */
	public List<Kazna> dohvatiKazneVozilaURasponu(String id, long odVremena, long doVremena) {
		WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("vozilo/{0}", id))
				.queryParam("od", odVremena).queryParam("do", doVremena);
		Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

		List<Kazna> kazne = new ArrayList<>();

		try {
			Response restOdgovor = zahtjev.get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				try (Jsonb jb = JsonbBuilder.create()) {
					var jsonResponse = jb.fromJson(odgovor, Map.class);
					Kazna[] pkazne = jb.fromJson(jb.toJson(jsonResponse.get("odgovor")),
							Kazna[].class);
					kazne.addAll(Arrays.asList(pkazne));
				}
			}
		} catch (Exception e) {
			return null;
		}

		return kazne;
	}

	/**
	 * Provjerava stanje poslužitelja za kazne.
	 *
	 * @return string s informacijom o stanju poslužitelja
	 */
	public String provjeriStanjePosluziteljaKazni() {
		Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);
		try (Response restOdgovor = zahtjev.head()) {
			return restOdgovor.getStatus() == 200 ? "PosluziteljKazni je aktivan."
					: "PosluziteljKazni nije aktivan.";
		} catch (Exception e) {
			return "REST servis nije dostupan.";
		}
	}
}
