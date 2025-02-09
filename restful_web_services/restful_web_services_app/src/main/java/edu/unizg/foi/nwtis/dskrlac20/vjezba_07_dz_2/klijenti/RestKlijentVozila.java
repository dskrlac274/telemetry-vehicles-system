package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.klijenti;

import java.util.concurrent.TimeUnit;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Vozila;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa {@code RestKlijentVozila} služi za komunikaciju s RESTful web servisom
 * koji upravlja vozilima.
 *
 * @author dskrlac20
 */
public class RestKlijentVozila {
	private final WebTarget webPredlozak;

	/**
	 * Konstruktor koji inicijalizira web cilj na osnovni URI web servisa i
	 * postavlja putanju do resursa vozila.
	 */
	public RestKlijentVozila() {
		String BASE_URI = "http://20.24.5.5:8080/";
		Client klijent = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS)
				.readTimeout(5, TimeUnit.SECONDS).build();
		webPredlozak = klijent.target(BASE_URI).path("nwtis/v1/api/vozila");
	}

	/**
	 * Metoda za dodavanje nove vožnje vozila na web servis.
	 *
	 * @param vozila objekt vozila koji se šalje na web servis
	 * @return true ako je vožnja uspješno dodana, false inače
	 */
	public boolean dodajVoznjuVozila(Vozila vozila) {
		Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);

		try (Response restOdgovor = zahtjev
				.post(Entity.entity(vozila, MediaType.APPLICATION_JSON))) {
			return restOdgovor.getStatus() == Response.Status.OK.getStatusCode();
		} catch (Exception e) {
			return false;
		}
	}
}
