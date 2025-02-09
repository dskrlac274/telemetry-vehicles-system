package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.klijenti;

import java.util.concurrent.TimeUnit;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Kazna;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa {@code RestKlijentKazne} služi za komunikaciju s RESTful web servisom
 * koji upravlja kaznama.
 *
 * @author dskrlac20
 */
public class RestKlijentKazne {
	private final WebTarget webPredlozak;

	/**
	 * Konstruktor koji inicijalizira web cilj na osnovni URI web servisa i
	 * postavlja putanju do resursa kazne.
	 */
	public RestKlijentKazne() {
		String BASE_URI = "http://20.24.5.5:8080/";
		Client klijent = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS)
				.readTimeout(5, TimeUnit.SECONDS).build();
		webPredlozak = klijent.target(BASE_URI).path("nwtis/v1/api/kazne");
	}

	/**
	 * Metoda za dodavanje nove kazne na web servis.
	 *
	 * @param kazna objekt kazne koji se šalje na web servis
	 * @return true ako je kazna uspješno dodana, false inače
	 */
	public boolean dodajKaznu(Kazna kazna) {
		Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);

		try (Response restOdgovor = zahtjev
				.post(Entity.entity(kazna, MediaType.APPLICATION_JSON))) {
			return restOdgovor.getStatus() == Response.Status.OK.getStatusCode();
		} catch (ProcessingException e) {
			return false;
		}
	}
}
