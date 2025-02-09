package edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import org.junit.jupiter.api.*;

import java.util.concurrent.ThreadFactory;

import static edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.testpomocnici.DatotecneOperacije.obrisiDatoteku;
import static edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.testpomocnici.MrezneOperacije.provjeriAktivnostPorta;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CentralniSustavTest {
	private static final String KONFIG_DATOTEKA = "NWTiS_DZ1_CS_Test.txt";
	private CentralniSustav centralniSustav;
	private final ThreadFactory tvornicaVirtualnihDretvi = Thread.ofVirtual().factory();

	@BeforeEach
	void setUp() {
		centralniSustav = new CentralniSustav();
	}

	@AfterEach
	void tearDown() {
		centralniSustav = null;
	}

	@Test
	@Order(1)
	void testProvjeriArgumentePokretanjaIspravaniPodaci() {
		String[] args = { KONFIG_DATOTEKA };
		assertDoesNotThrow(() -> CentralniSustav.provjeriArgumentePokretanja(args));
	}

	@Test
	@Order(2)
	void testProvjeriArgumentePokretanjaSaNedovoljnoArgumenata() {
		String[] args = {};
		assertThrows(IllegalArgumentException.class, () -> CentralniSustav.provjeriArgumentePokretanja(args));
	}

	@Test
	@Order(3)
	void testProvjeriArgumentePokretanjaSaPreviseArgumenata() {
		String[] args = { "konfig1.txt", "konfig2.txt" };
		assertThrows(IllegalArgumentException.class, () -> CentralniSustav.provjeriArgumentePokretanja(args));
	}

	@Test
	@Order(4)
	void testPreuzmiPostavkeNeispravnaDatoteka() {
		String datotekaKonfiguracije = "neispravna_konfiguracija.pptx";
		assertThrows(NeispravnaKonfiguracija.class, () -> centralniSustav.preuzmiPostavke(datotekaKonfiguracije));
		obrisiDatoteku(KONFIG_DATOTEKA);
	}

	@Test
	@Order(5)
	void testPreuzmiPostavkeIspravniPodaci() {
		try {
			Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(KONFIG_DATOTEKA);
			konfig.spremiPostavku("mreznaVrataRadara", "8000");
			konfig.spremiPostavku("mreznaVrataVozila", "8001");
			konfig.spremiPostavku("mreznaVrataNadzora", "8002");
			konfig.spremiPostavku("maksVozila", "2");
			konfig.spremiKonfiguraciju();

			this.centralniSustav.preuzmiPostavke(KONFIG_DATOTEKA);
		} catch (NeispravnaKonfiguracija e) {
			Thread.currentThread().interrupt();
		}
		obrisiDatoteku(KONFIG_DATOTEKA);

		assertEquals(8000, this.centralniSustav.mreznaVrataRadara);
		assertEquals(8001, this.centralniSustav.mreznaVrataVozila);
		assertEquals(8002, this.centralniSustav.mreznaVrataNadzora);
		assertEquals(2, this.centralniSustav.maksVozila);
	}

	@Test
	@Order(6)
	void testPreuzmiPostavkeNeispravniPodaci() {
		assertThrows(NumberFormatException.class, () -> {
			Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(KONFIG_DATOTEKA);
			konfig.spremiPostavku("mreznaVrataRadara", "pogresna vrijednost");
			konfig.spremiPostavku("mreznaVrataVozila", "pogresna vrijednost");
			konfig.spremiPostavku("mreznaVrataNadzora", "pogresna vrijednost");
			konfig.spremiPostavku("maksVozila", "pogresna vrijednost");
			konfig.spremiKonfiguraciju();

			centralniSustav.preuzmiPostavke(KONFIG_DATOTEKA);
		});

		obrisiDatoteku(KONFIG_DATOTEKA);
	}

	@Test
	@Order(7)
	void testPokreniPosluziteljeProvjeriSlusanjeUspjesno() {
		this.centralniSustav.mreznaVrataRadara = 8000;
		this.centralniSustav.mreznaVrataVozila = 8001;

		Thread testnaDretva = tvornicaVirtualnihDretvi.newThread(() -> centralniSustav.pokreniPosluzitelje());
		testnaDretva.start();

		assertTrue(provjeriAktivnostPorta("localhost", this.centralniSustav.mreznaVrataRadara));
		assertTrue(provjeriAktivnostPorta("localhost", this.centralniSustav.mreznaVrataVozila));

		testnaDretva.interrupt();
		try {
			testnaDretva.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Test
	@Order(8)
	void testPokreniPosluziteljaNeaktivan() {
		int vrataRadara = 8000;
		int vrataVozila = 8001;
		try {
			Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(KONFIG_DATOTEKA);
			konfig.spremiPostavku("mreznaVrataRadara", Integer.toString(vrataRadara));
			konfig.spremiPostavku("mreznaVrataVozila", Integer.toString(vrataVozila));
			konfig.spremiPostavku("mreznaVrataNadzora", "8300");
			konfig.spremiPostavku("maksVozila", "2");
			konfig.spremiKonfiguraciju();
		} catch (NeispravnaKonfiguracija e) {
			fail("Konfiguracija nije mogla biti kreirana.");
		}

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		assertFalse(provjeriAktivnostPorta("localhost", vrataRadara));
		assertFalse(provjeriAktivnostPorta("localhost", vrataVozila));
	}

	@Test
	@Order(9)
	void testPokreniPosluziteljaAktivan() {
		int vrataRadara = 8000;
		int vrataVozila = 8001;

		String[] argumenti = { KONFIG_DATOTEKA };
		this.centralniSustav.mreznaVrataRadara = vrataRadara;
		this.centralniSustav.mreznaVrataVozila = vrataVozila;
		Thread testnaDretva = tvornicaVirtualnihDretvi.newThread(() -> CentralniSustav.main(argumenti));
		testnaDretva.start();

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		assertTrue(provjeriAktivnostPorta("localhost", vrataRadara));
		assertTrue(provjeriAktivnostPorta("localhost", vrataVozila));

		obrisiDatoteku(KONFIG_DATOTEKA);
		testnaDretva.interrupt();
		try {
			testnaDretva.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
