package edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.radnici;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.CentralniSustav;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.PosluziteljRadara;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.PosluziteljZaVozila;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import org.junit.jupiter.api.*;

import java.util.concurrent.ThreadFactory;

import static edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.testpomocnici.DatotecneOperacije.obrisiDatoteku;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RadnikZaVozilaTest {
	private static final String KONFIG_DATOTEKA = "NWTiS_DZ1_R_Test.txt";
	private PosluziteljZaVozila posluziteljZaVozila;
	private RadnikZaVozila radnikZaVozila;
	private CentralniSustav centralniSustav;
	private Thread posluziteljZaVozilaDretva;
	private final ThreadFactory tvornicaVirtualnihDretvi = Thread.ofVirtual().factory();

	@BeforeEach
	void setUp() {
		konfigurirajISpremiPostavke();
		posluziteljZaVozila = new PosluziteljZaVozila(centralniSustav);
		posluziteljZaVozilaDretva = tvornicaVirtualnihDretvi.newThread(posluziteljZaVozila);
		posluziteljZaVozilaDretva.start();
		radnikZaVozila = new RadnikZaVozila(posluziteljZaVozila, null);
	}

	@AfterEach
	void afterEach() {
		try {
			PosluziteljZaVozila.aktivniKanali.clear();
			posluziteljZaVozilaDretva.interrupt();
			Thread.sleep(150);
		} catch (InterruptedException e) {
			fail("Test je pao iz razloga" + e.getMessage());
		}
	}

	@AfterEach
	void tearDown() {
		posluziteljZaVozila = null;
		radnikZaVozila = null;
		centralniSustav = null;
		posluziteljZaVozilaDretva.interrupt();
	}

	@Test
	@Order(1)
	void testIspravanRegexZahtjevVozila() {
		String ispravnaKomanda = "VOZILO 1 101 1708073749078 0.02 0.8086 0.02 214.2 1.337297 19 93 40.43 7314 20 "
				+ "27.9 816.458 46.286644 16.35285";
		assertTrue(this.radnikZaVozila.predlozakZahtjevaVozila.matcher(ispravnaKomanda).matches());
	}

	@Test
	@Order(2)
	void testNeispravanRegexZahtjevVozilaNeispravniFormat() {
		String neispravnaKomanda = "VOZILO 1 101 1708073749078 broj 0.8086 0.02 214.2 1.337297 19 93 40.43 7314 "
				+ "20 27.9 816.458 46.286644 16.35285";
		assertFalse(this.radnikZaVozila.predlozakZahtjevaVozila.matcher(neispravnaKomanda).matches());
	}

	@Test
	@Order(3)
	void testNeispravanRegexZahtjevVozilaNegativanBroj() {
		String neispravnaKomanda = "VOZILO 123 1609459200000 1609459205000 -120 -45.815399 15.966568 -45.800000 "
				+ "15.950000";
		assertFalse(this.radnikZaVozila.predlozakZahtjevaVozila.matcher(neispravnaKomanda).matches());
	}

	@Test
	@Order(4)
	void testEvidentirajPodatkeVozila() {
		String zahtjev = "VOZILO 1 101 1708073749078 0.02 0.8086 0.02 214.2 1.337297 19 93 40.43 7314 20 27.9 "
				+ "816.458 46.286644 16.35285";

		radnikZaVozila.poklapanjeDosegaRadara = radnikZaVozila.predlozakZahtjevaVozila.matcher(zahtjev);
		assertTrue(radnikZaVozila.poklapanjeDosegaRadara.matches());

		radnikZaVozila.evidentirajPodatkeVozila();

		assertNotNull(radnikZaVozila.podaciVozila);
		assertEquals(1, radnikZaVozila.podaciVozila.id());
		assertEquals(1708073749078L, radnikZaVozila.podaciVozila.vrijeme());
		assertEquals(0.02, radnikZaVozila.podaciVozila.brzina());
		assertEquals(46.286644, radnikZaVozila.podaciVozila.gpsSirina());
		assertEquals(16.35285, radnikZaVozila.podaciVozila.gpsDuzina());
	}

	@Test
	@Order(5)
	void testDohvatiRadaraZaVoziloPronadeniRadar() {
		String zahtjev = "VOZILO 1 101 1708073749078 0.02 0.8086 0.02 214.2 1.337297 19 93 40.43 7314 20 27.9 816.458 "
				+ "46.286644 16.35285";

		radnikZaVozila.poklapanjeDosegaRadara = radnikZaVozila.predlozakZahtjevaVozila.matcher(zahtjev);
		assertTrue(radnikZaVozila.poklapanjeDosegaRadara.matches());

		radnikZaVozila.evidentirajPodatkeVozila();

		PodaciRadara radar = new PodaciRadara(1, "localhost", 8000, 120, 180, 15, "localhost", 8001, "localhost", 8002,
				"123 Radar Lane", 46.286644, 16.35285);
		posluziteljZaVozila.centralniSustav.registriraniRadari.put(1, radar);

		PodaciRadara dobiveniRadar = radnikZaVozila.dohvatiRadaraZaVozilo();

		assertNotNull(dobiveniRadar);
		assertEquals(1, dobiveniRadar.id());
	}

	@Test
	@Order(6)
	void testDohvatiRadaraZaVoziloNemaRadara() {
		String zahtjev = "VOZILO 1 101 1708073749078 0.02 0.8086 0.02 214.2 1.337297 19 93 40.43 7314 20 27.9 816.458 "
				+ "46.286644 16.35285";

		radnikZaVozila.poklapanjeDosegaRadara = radnikZaVozila.predlozakZahtjevaVozila.matcher(zahtjev);
		assertTrue(radnikZaVozila.poklapanjeDosegaRadara.matches());

		radnikZaVozila.evidentirajPodatkeVozila();

		PodaciRadara radar = new PodaciRadara(1, "localhost", 8000, 120, 180, 15, "localhost", 8001, "localhost", 8002,
				"123 Radar Lane", 34.286644, 14.35285);
		posluziteljZaVozila.centralniSustav.registriraniRadari.put(1, radar);

		PodaciRadara dobiveniRadar = radnikZaVozila.dohvatiRadaraZaVozilo();

		assertNull(dobiveniRadar);
	}

	@Test
	@Order(7)
	void testPosaljiZahtjevPosluziteljuRadaraNemaOdgovora() {
		PodaciRadara radar = new PodaciRadara(1, "localhost", 8000, 120, 180, 15, "localhost", 8001, "localhost", 8002,
				"123 Radar Lane", 46.286644, 16.35285);
		String zahtjev = "VOZILO 1 101 1708073749078 0.02 0.8086 0.02 214.2 1.337297 19 93 40.43 7314 20 27.9 816.458 "
				+ "46.286644 16.35285";

		radnikZaVozila.poklapanjeDosegaRadara = radnikZaVozila.predlozakZahtjevaVozila.matcher(zahtjev);
		assertTrue(radnikZaVozila.poklapanjeDosegaRadara.matches());
		radnikZaVozila.evidentirajPodatkeVozila();

		String odgovor = radnikZaVozila.posaljiZahtjevPosluziteljuRadara(radar);

		assertNull(odgovor);
	}

	@Test
	@Order(8)
	void testPosaljiZahtjevPosluziteljuRadaraUspjesanOdgovor() {
		String zahtjev = "VOZILO 1 101 1708073749078 0.02 0.8086 0.02 214.2 1.337297 19 93 40.43 7314 20 27.9 816.458 "
				+ "46.286644 16.35285";

		PodaciRadara radar = new PodaciRadara(1, "localhost", 8010, 120, 180, 15, "localhost", 8001, "localhost", 8002,
				"123 Radar Lane", 46.286644, 16.35285);

		var posluziteljRadara = new PosluziteljRadara();
		posluziteljRadara.podaciRadara = radar;
		radnikZaVozila.poklapanjeDosegaRadara = radnikZaVozila.predlozakZahtjevaVozila.matcher(zahtjev);
		assertTrue(radnikZaVozila.poklapanjeDosegaRadara.matches());
		radnikZaVozila.evidentirajPodatkeVozila();

		Thread testnaDretva = tvornicaVirtualnihDretvi.newThread(posluziteljRadara::pokreniPosluzitelja);
		testnaDretva.start();

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		String odgovor = radnikZaVozila.posaljiZahtjevPosluziteljuRadara(radar);

		testnaDretva.interrupt();
		assertNotNull(odgovor);
	}

	@Test
	@Order(9)
	void testObradiZahtjevUspjesan() {
		String zahtjev = "VOZILO 1 101 1708073749078 0.02 0.8086 0.02 214.2 1.337297 19 93 40.43 7314 20 "
				+ "27.9 816.458 46.286644 16.35285";

		Thread testnaDretva = tvornicaVirtualnihDretvi.newThread(() -> radnikZaVozila.obradiZahtjev(zahtjev));
		testnaDretva.start();

		try {
			testnaDretva.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		assertNotNull(radnikZaVozila.podaciVozila);
		testnaDretva.interrupt();
	}

	@Test
	@Order(10)
	void testObradiZahtjevNeuspjesan() {
		String zahtjev = "VOZILO 1 KRIVI FORMAT";

		Thread testnaDretva = tvornicaVirtualnihDretvi.newThread(() -> radnikZaVozila.obradiZahtjev(zahtjev));
		testnaDretva.start();
		try {
			testnaDretva.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		assertNull(radnikZaVozila.podaciVozila);
	}

	private void konfigurirajISpremiPostavke() {
		centralniSustav = new CentralniSustav();
		try {
			Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(KONFIG_DATOTEKA);
			konfig.spremiPostavku("mreznaVrataRadara", "8000");
			konfig.spremiPostavku("mreznaVrataVozila", "8001");
			konfig.spremiPostavku("mreznaVrataNadzora", "8002");
			konfig.spremiPostavku("maksVozila", "2");
			konfig.spremiKonfiguraciju();
			centralniSustav.preuzmiPostavke(KONFIG_DATOTEKA);
		} catch (NeispravnaKonfiguracija e) {
			Thread.currentThread().interrupt();
		}
		obrisiDatoteku(KONFIG_DATOTEKA);
	}
}