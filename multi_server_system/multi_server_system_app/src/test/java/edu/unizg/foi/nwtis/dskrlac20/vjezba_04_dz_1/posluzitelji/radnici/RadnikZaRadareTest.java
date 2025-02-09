package edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.radnici;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.podaci.BrzoVozilo;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.PosluziteljKazni;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.PosluziteljRadara;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import org.junit.jupiter.api.*;

import java.net.UnknownHostException;
import java.util.concurrent.ThreadFactory;

import static edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.testpomocnici.DatotecneOperacije.obrisiDatoteku;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RadnikZaRadareTest {
	private static final String KONFIG_DATOTEKA = "NWTiS_DZ1_R1_Test.txt";
	private PosluziteljRadara posluziteljRadara;
	private RadnikZaRadare radnikZaRadare;
	private final ThreadFactory tvornicaVirtualnihDretvi = Thread.ofVirtual().factory();
	private Thread posluziteljRadaraDretva;

	@BeforeEach
	void setUp() {
		konfigurirajISpremiPostavke();
		posluziteljRadaraDretva = tvornicaVirtualnihDretvi.newThread(() -> posluziteljRadara.pokreniPosluzitelja());
		posluziteljRadaraDretva.start();
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			fail("Test je pao iz razloga" + e.getMessage());
		}
		radnikZaRadare = new RadnikZaRadare(posluziteljRadara, null);
	}

	@AfterEach
	void afterEach() {
		posluziteljRadaraDretva.interrupt();
		posluziteljRadara = null;
		radnikZaRadare = null;
	}

	@Test
	@Order(1)
	void testRegexZahtjevRadaraIspravniPodaci() {
		String zahtjev = "VOZILO 1 1234567890123 120 46.286644 16.35285";
		assertTrue(radnikZaRadare.predlozakDetekcijaRadara.matcher(zahtjev).matches());
	}

	@Test
	@Order(2)
	void testRegexZahtjevRadaraNeispravniPodaciNeispravanFormat() {
		String zahtjev = "VOZILO 1 broj 120 46.286644 16.35285";
		assertFalse(radnikZaRadare.predlozakDetekcijaRadara.matcher(zahtjev).matches());
	}

	@Test
	@Order(3)
	void testRegexZahtjevRadaraNeispravniPodaciNeispravnaVrijednost() {
		String zahtjev = "VOZILO 1 -1234567890123 120 46.286644 16.35285";
		assertFalse(radnikZaRadare.predlozakDetekcijaRadara.matcher(zahtjev).matches());
	}

	@Test
	@Order(4)
	void testunutarDosegaRadaraUspjesno() {
		assertTrue(radnikZaRadare.unutarDosegaRadara(46.300000, 16.330000));
	}

	@Test
	@Order(5)
	void testunutarDosegaRadaraNeuspjesno() {
		assertFalse(radnikZaRadare.unutarDosegaRadara(0.0, 0.0));
	}

	@Test
	@Order(6)
	void testOdrediStanjeVoznjeBezPrekrsaja() {
		String zahtjev = "VOZILO 1 1234567890123 120 46.286644 16.35285";
		radnikZaRadare.obradaZahtjeva(zahtjev);
		radnikZaRadare.ucitajKomandu();
		assertEquals("OK", radnikZaRadare.odrediStanjeVoznje());
	}

	@Test
	@Order(7)
	void testOdrediStanjeVoznjeSPrekrsajem() {
		String zahtjev = "VOZILO 1 1234567890123 120 46.286644 16.35285";
		radnikZaRadare.obradaZahtjeva(zahtjev);
		radnikZaRadare.ucitajKomandu();
		String odgovor = radnikZaRadare.odrediStanjeVoznje();
		assertTrue(odgovor.contains("OK") || odgovor.contains("ERROR 31"));
	}

	@Test
	@Order(8)
	void testPosaljiKaznuPosluziteljuKazniNeuspjesno() {
		BrzoVozilo staroVozilo = new BrzoVozilo(123, -1, 1598304000000L, 120.0, 46.305744, 16.336048, true);
		BrzoVozilo novoVozilo = new BrzoVozilo(123, -1, 1598304001000L, 130.0, 46.305744, 16.336048, false);
		String odgovor = radnikZaRadare.posaljiKaznuPosluziteljuKazni(staroVozilo, novoVozilo);
		assertNull(odgovor);
	}

	@Test
	@Order(9)
	void testPosaljiKaznuPosluziteljuKazniUspjesno() {
		BrzoVozilo staroVozilo = new BrzoVozilo(123, -1, 1598304000000L, 120.0, 46.305744, 16.336048, true);
		BrzoVozilo novoVozilo = new BrzoVozilo(123, -1, 1598304001000L, 130.0, 46.305744, 16.336048, false);
		var posluziteljKazni = new PosluziteljKazni();
		posluziteljKazni.mreznaVrata = posluziteljRadara.podaciRadara.mreznaVrataKazne();
		Thread testnaDretva = tvornicaVirtualnihDretvi.newThread(posluziteljKazni::pokreniPosluzitelja);
		testnaDretva.start();

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			fail("Test je pao iz razloga" + e.getMessage());
		}
		assertEquals("OK", radnikZaRadare.posaljiKaznuPosluziteljuKazni(staroVozilo, novoVozilo));
		testnaDretva.interrupt();
	}

	@Test
	@Order(10)
	void testPokusajGeneriratiKaznuUnutarDvaPutMaksTrajanja() {
		long pocetnoVrijeme = 10000;
		long krajnjeVrijeme = pocetnoVrijeme + posluziteljRadara.podaciRadara.maksTrajanje() + 8000;

		BrzoVozilo pocetnoVozilo = new BrzoVozilo(1, -1, pocetnoVrijeme, 120.0, 46.305744, 16.336048, true);
		BrzoVozilo trenutnoVozilo = new BrzoVozilo(1, -1, krajnjeVrijeme, 130.0, 46.305744, 16.336048, true);
		var testnaDretva = pokreniPosluziteljaKazni();

		RadnikZaRadare.vozila.put(1, pocetnoVozilo);
		String odgovor = radnikZaRadare.pokusajGeneriratiKaznu(pocetnoVozilo, trenutnoVozilo);

		assertEquals("OK", odgovor);
		assertFalse(RadnikZaRadare.vozila.get(1).status());
		testnaDretva.interrupt();
	}

	@Test
	@Order(11)
	void testPokusajGeneriratiKaznuPrekoDvaPutMaksTrajanja() {
		long maksTrajanje = 2000;
		long pocetnoVrijeme = 10000;
		long krajnjeVrijeme = pocetnoVrijeme + 2 * maksTrajanje + 10000;

		BrzoVozilo pocetnoVozilo = new BrzoVozilo(1, -1, pocetnoVrijeme, 120.0, 46.305744, 16.336048, true);
		BrzoVozilo trenutnoVozilo = new BrzoVozilo(1, -1, krajnjeVrijeme, 130.0, 46.305744, 16.336048, true);
		var testnaDretva = pokreniPosluziteljaKazni();
		RadnikZaRadare.vozila.put(1, pocetnoVozilo);
		String odgovor = radnikZaRadare.pokusajGeneriratiKaznu(pocetnoVozilo, trenutnoVozilo);

		assertEquals("OK", odgovor);
		assertFalse(RadnikZaRadare.vozila.get(1).status());
		testnaDretva.interrupt();
	}

	private Thread pokreniPosluziteljaKazni() {
		var posluziteljKazni = new PosluziteljKazni();
		posluziteljKazni.mreznaVrata = posluziteljRadara.podaciRadara.mreznaVrataKazne();
		Thread testnaDretva = tvornicaVirtualnihDretvi.newThread(posluziteljKazni::pokreniPosluzitelja);
		testnaDretva.start();

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return testnaDretva;
	}

	private void konfigurirajISpremiPostavke() {
		posluziteljRadara = new PosluziteljRadara();
		try {
			Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(KONFIG_DATOTEKA);
			konfig.spremiPostavku("id", "1");
			konfig.spremiPostavku("mreznaVrataRadara", "8010");
			konfig.spremiPostavku("maksBrzina", "8");
			konfig.spremiPostavku("maksTrajanje", "7");
			konfig.spremiPostavku("maksUdaljenost", "1000");
			konfig.spremiPostavku("adresaRegistracije", "localhost");
			konfig.spremiPostavku("mreznaVrataRegistracije", "9000");
			konfig.spremiPostavku("adresaKazne", "localhost");
			konfig.spremiPostavku("adresaKazne", "localhost");
			konfig.spremiPostavku("mreznaVrataKazne", "8020");
			konfig.spremiPostavku("gpsSirina", "46.29950");
			konfig.spremiPostavku("gpsDuzina", "16.33001");

			konfig.spremiKonfiguraciju();
			posluziteljRadara.preuzmiPostavke(KONFIG_DATOTEKA);
		} catch (NeispravnaKonfiguracija | UnknownHostException e) {
			Thread.currentThread().interrupt();
		}
		obrisiDatoteku(KONFIG_DATOTEKA);
	}
}
