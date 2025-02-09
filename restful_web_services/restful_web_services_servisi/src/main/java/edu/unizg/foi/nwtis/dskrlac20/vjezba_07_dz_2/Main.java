package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.core.Application;

/**
 * Klasa {@code Main} pokreće JAX-RS aplikaciju kao samostalni server.
 *
 * @author dskrlac20
 */
@ApplicationPath("")
public class Main extends Application {

	/**
	 * Glavna metoda koja pokreće RESTful web servis.
	 *
	 * @param args argumenti komandne linije
	 * @throws Exception ako dođe do greške prilikom pokretanja servera
	 */
	public static void main(String[] args) throws Exception {
		Application app = new Main();
		SeBootstrap.Configuration config = SeBootstrap.Configuration.builder().host("0.0.0.0")
				.rootPath("").port(8080).build();

		SeBootstrap.start(app, config).thenAccept(instance -> {
			instance.stopOnShutdown(stopResult -> stopResult.unwrap(Object.class));
			System.out.printf("\nREST servis na adresi: %s\n", instance.configuration().baseUri());
		});

		Thread.currentThread().join();
	}
}
