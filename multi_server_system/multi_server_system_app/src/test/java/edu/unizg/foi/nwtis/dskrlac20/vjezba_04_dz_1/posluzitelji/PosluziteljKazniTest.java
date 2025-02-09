package edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.podaci.PodaciKazne;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import org.junit.jupiter.api.*;

import java.util.concurrent.ThreadFactory;

import static edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.testpomocnici.DatotecneOperacije.obrisiDatoteku;
import static edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.testpomocnici.MrezneOperacije.provjeriAktivnostPorta;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PosluziteljKazniTest {
	private static final String KONFIG_DATOTEKA = "NWTiS_DZ1_PK_Test.txt";
	private PosluziteljKazni posluziteljKazni;
	private final ThreadFactory tvornicaVirtualnihDretvi = Thread.ofVirtual().factory();

	@BeforeEach
	void setUp() {
		posluziteljKazni = new PosluziteljKazni();
		posluziteljKazni.sveKazne.add(
				new PodaciKazne(1, 1609459200000L, 1609459300000L, 80.0, 45.815399, 15.966568, 45.800000, 15.950000));
		posluziteljKazni.sveKazne.add(
				new PodaciKazne(1, 1609459300000L, 1609459400000L, 85.0, 45.815500, 15.966669, 45.800100, 15.950100));
		posluziteljKazni.sveKazne.add(
				new PodaciKazne(2, 1609459200000L, 1609459300000L, 90.0, 45.815600, 15.966770, 45.800200, 15.950200));
	}

	@AfterEach
	void tearDown() {
		posluziteljKazni = null;
	}

	@Test
	@Order(1)
	void testIspravanRegexKazna() {
		String validInput = "VOZILO 123 1609459200000 1609459205000 120.5 -45.815399 15.966568 -45.800000 15.950000";
		assertTrue(this.posluziteljKazni.predlozakKazna.matcher(validInput).matches());
	}

	@Test
	@Order(2)
	void testNeispravanRegexKaznaNeispravniFormat() {
		String invalidInput = "VOZILO 123 1609459200000 1609459205000 broj -45.815399 15.966568 -45.800000 15.950000";
		assertFalse(this.posluziteljKazni.predlozakKazna.matcher(invalidInput).matches());
	}

	@Test
	@Order(3)
	void testNeispravanRegexKaznaNegativanBroj() {
		String invalidInput = "VOZILO 123 1609459200000 -1609459205000 120 -45.815399 15.966568 -45.800000 15.950000";
		assertFalse(this.posluziteljKazni.predlozakKazna.matcher(invalidInput).matches());
	}

	@Test
	@Order(4)
	void testIspravanRegexKaznaDetaljiKazne() {
		String ispravnaKomanda = "VOZILO 456 1609459200000 1609459300000";
		assertTrue(this.posluziteljKazni.predlozakDetaljiKazne.matcher(ispravnaKomanda).matches());
	}

	@Test
	@Order(5)
	void testNeispravanRegexDetaljiKazneNeispravniFormat() {
		String neispravnaKomanda = "VOZILO 456 od do";
		assertFalse(this.posluziteljKazni.predlozakDetaljiKazne.matcher(neispravnaKomanda).matches());
	}

	@Test
	@Order(6)
	void testIspravanRegexDetaljiKazneNegativanBroj() {
		String neispravnaKomanda = "VOZILO 456 1609459200000 -1609459200000";
		assertFalse(this.posluziteljKazni.predlozakDetaljiKazne.matcher(neispravnaKomanda).matches());
	}

	@Test
	@Order(7)
	void testIspravanRegexStatistika() {
		String ispravnaKomanda = "STATISTIKA 1609459200000 1609459300000";
		assertTrue(this.posluziteljKazni.predlozakStatistika.matcher(ispravnaKomanda).matches());
	}

	@Test
	@Order(8)
	void testNeispravanRegexStatistikaNeispravniFormat() {
		String neispravnaKomanda = "STATISTIKA od 1609459300000";
		assertFalse(this.posluziteljKazni.predlozakStatistika.matcher(neispravnaKomanda).matches());
	}

	@Test
	@Order(9)
	void testNeispravanRegexStatistikaNegativanBroj() {
		String neispravnaKomanda = "STATISTIKA 1609459200000 -1609459300000";
		assertFalse(this.posluziteljKazni.predlozakStatistika.matcher(neispravnaKomanda).matches());
	}

	@Test
	@Order(10)
	void testProvjeriArgumentePokretanjaIspravniPodaci() {
		String[] args = { KONFIG_DATOTEKA };
		assertDoesNotThrow(() -> PosluziteljKazni.provjeriArgumentePokretanja(args));
	}

	@Test
	@Order(11)
	void testProvjeriArgumentePokretanjaSaNedovoljnoArgumenata() {
		String[] args = {};
		assertThrows(IllegalArgumentException.class, () -> PosluziteljKazni.provjeriArgumentePokretanja(args));
	}

	@Test
	@Order(12)
	void testProvjeriArgumentePokretanjaSaPreviseArgumenata() {
		String[] args = { "konfig1.txt", "konfig2.txt" };
		assertThrows(IllegalArgumentException.class, () -> PosluziteljKazni.provjeriArgumentePokretanja(args));
	}

	@Test
	@Order(13)
	void testPreuzmiPostavkeNeispravnaDatoteka() {
		String datotekaKonfiguracije = "neispravna_konfiguracija.pptx";
		assertThrows(NeispravnaKonfiguracija.class, () -> posluziteljKazni.preuzmiPostavke(datotekaKonfiguracije));
		obrisiDatoteku(KONFIG_DATOTEKA);
	}

	@Test
	@Order(14)
	void testPreuzmiPostavkeIspravniPodaci() {
		try {
			Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(KONFIG_DATOTEKA);
			konfig.spremiPostavku("mreznaVrataKazne", "8020");
			konfig.spremiKonfiguraciju();

			this.posluziteljKazni.preuzmiPostavke(KONFIG_DATOTEKA);
		} catch (NeispravnaKonfiguracija e) {
			fail("Konfiguracija nije mogla biti kreirana.");
		}
		obrisiDatoteku(KONFIG_DATOTEKA);

		assertEquals(8020, this.posluziteljKazni.mreznaVrata);
	}

	@Test
	@Order(15)
	void testPreuzmiPostavkeNeispravniPodaci() {
		assertThrows(NumberFormatException.class, () -> {
			Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(KONFIG_DATOTEKA);
			konfig.spremiPostavku("mreznaVrataKazne", "pogresna vrijednost");
			konfig.spremiKonfiguraciju();

			this.posluziteljKazni.preuzmiPostavke(KONFIG_DATOTEKA);
		});

		obrisiDatoteku(KONFIG_DATOTEKA);
	}

	@Test
	@Order(16)
	void testObradaKaznaIspravniPodaci() {
		String zahtjev = "VOZILO 3 1609459400000 1609459500000 105.0 45.815400 15.966569 45.800001 15.950001";
		posluziteljKazni.poklapanjeKazna = posluziteljKazni.predlozakKazna.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeKazna.matches());
		assertEquals("OK", posluziteljKazni.obradaKazna());
		assertEquals(4, posluziteljKazni.sveKazne.size());
	}

	@Test
	@Order(17)
	void testObradaDetaljiKazneIspravniPodaci() {
		String zahtjev = "VOZILO 1 1609459200000 1609459300000";
		posluziteljKazni.poklapanjeDetaljiKazne = posluziteljKazni.predlozakDetaljiKazne.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeDetaljiKazne.matches());
		assertTrue(posluziteljKazni.obradaDetaljiKazne().startsWith("OK"));
	}

	@Test
	@Order(18)
	void testObradaDetaljiKazneNemaPodataka() {
		String zahtjev = "VOZILO 3 100999459500000 100999459500005";
		posluziteljKazni.poklapanjeDetaljiKazne = posluziteljKazni.predlozakDetaljiKazne.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeDetaljiKazne.matches());
		assertEquals("ERROR 41 Nema kazne za zadano vozilo u zadanom periodu.", posluziteljKazni.obradaDetaljiKazne());
	}

	@Test
	@Order(19)
	void testDohvatiPodatkeKazneIspravniPodaci() {
		String zahtjev = "VOZILO 1 1609459200000 1609459400000";
		posluziteljKazni.poklapanjeDetaljiKazne = posluziteljKazni.predlozakDetaljiKazne.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeDetaljiKazne.matches());
		PodaciKazne result = posluziteljKazni.dohvatiPodatkeKazne();
		assertNotNull(result);
		assertEquals(1609459300000L, result.vrijemePocetak());
	}

	@Test
	@Order(20)
	void testDohvatiPodatkeKazneNemaPodataka() {
		String zahtjev = "VOZILO 3 1609459200000 1609459300000";
		posluziteljKazni.poklapanjeDetaljiKazne = posluziteljKazni.predlozakDetaljiKazne.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeDetaljiKazne.matches());
		assertNull(posluziteljKazni.dohvatiPodatkeKazne());
	}

	@Test
	@Order(21)
	void testDohvatiPodatkeKaznePodaciNaGranicama() {
		String zahtjev = "VOZILO 1 1609459300000 1609459400000";
		posluziteljKazni.poklapanjeDetaljiKazne = posluziteljKazni.predlozakDetaljiKazne.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeDetaljiKazne.matches());
		PodaciKazne rezultat = posluziteljKazni.dohvatiPodatkeKazne();
		assertNotNull(rezultat);
		assertEquals(1609459300000L, rezultat.vrijemePocetak());
	}

	@Test
	@Order(22)
	void testObradaStatistikeIspravniPodaci() {
		String zahtjev = "STATISTIKA 1609459200000 1609459300000";
		posluziteljKazni.poklapanjeStatistika = posluziteljKazni.predlozakStatistika.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeStatistika.matches());
		assertEquals("OK 1 1; 2 1;", posluziteljKazni.obradaStatistike());
	}

	@Test
	@Order(23)
	void testObradaStatistikeNemaPodataka() {
		String zahtjev = "STATISTIKA 100999459500000 100999459500005";
		posluziteljKazni.poklapanjeStatistika = posluziteljKazni.predlozakStatistika.matcher(zahtjev);
		posluziteljKazni.sveKazne.clear();
		assertTrue(posluziteljKazni.poklapanjeStatistika.matches());
		assertEquals("ERROR 49 Nema definiranih vozila u zadanom intervalu.", posluziteljKazni.obradaStatistike());
	}

	@Test
	@Order(24)
	void testObradaStatistikePrazniPodaci() {
		String zahtjev = "STATISTIKA 100999459500000 100999459500005";
		posluziteljKazni.poklapanjeStatistika = posluziteljKazni.predlozakStatistika.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeStatistika.matches());
		assertEquals("OK 1 0; 2 0;", posluziteljKazni.obradaStatistike());
	}

	@Test
	@Order(25)
	void testDohvatiStatistikuKazniIspravniPodaci() {
		String zahtjev = "STATISTIKA 1609459200000 1609459400000";
		posluziteljKazni.poklapanjeStatistika = posluziteljKazni.predlozakStatistika.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeStatistika.matches());

		var statistika = posluziteljKazni.dohvatiStatistikuKazni();
		assertNotNull(statistika);
		assertEquals(2, statistika.get(1));
		assertEquals(1, statistika.get(2));
	}

	@Test
	@Order(26)
	void testDohvatiStatistikuKazniNemaPodataka() {
		String zahtjev = "STATISTIKA 100999459500000 100999459500005";
		posluziteljKazni.poklapanjeStatistika = posluziteljKazni.predlozakStatistika.matcher(zahtjev);
		posluziteljKazni.sveKazne.clear();
		assertTrue(posluziteljKazni.poklapanjeStatistika.matches());
		assertEquals(0, posluziteljKazni.dohvatiStatistikuKazni().size());
	}

	@Test
	@Order(27)
	void testDohvatiStatistikuKazniNaGranicama() {
		String zahtjev = "STATISTIKA 1609459200000 1609459300000";
		posluziteljKazni.poklapanjeStatistika = posluziteljKazni.predlozakStatistika.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeStatistika.matches());

		var statistika = posluziteljKazni.dohvatiStatistikuKazni();
		assertNotNull(statistika);
		assertEquals(1, statistika.get(1));
		assertEquals(1, statistika.get(2));
	}

	@Test
	@Order(28)
	void testObradiZahtjevObradaKaznaIspravniPodaci() {
		String zahtjev = "VOZILO 123 1609459200000 1609459205000 120.5 -45.815399 15.966568 -45.800000 15.950000";
		assertEquals("OK", posluziteljKazni.obradiZahtjev(zahtjev));
	}

	@Test
	@Order(29)
	void testObradiZahtjevObradaDetaljiKazneIspravniPodaci() {
		String zahtjev = "VOZILO 123 100999459500000 100999459500005";
		posluziteljKazni.poklapanjeDetaljiKazne = posluziteljKazni.predlozakDetaljiKazne.matcher(zahtjev);
		assertTrue(posluziteljKazni.poklapanjeDetaljiKazne.matches());
		assertEquals("ERROR 41 Nema kazne za zadano vozilo u zadanom periodu.",
				posluziteljKazni.obradiZahtjev(zahtjev));
	}

	@Test
	@Order(30)
	void testObradiZahtjevNeispravnaKomanda() {
		String zahtjev = "POGRESKA";
		assertEquals("ERROR 40 Neispravan format komande.", posluziteljKazni.obradiZahtjev(zahtjev));
	}

	@Test
	@Order(31)
	void testPokreniPosluziteljeProvjeriSlusanjeUspjesno() {
		this.posluziteljKazni.mreznaVrata = 8020;

		Thread testnaDretva = tvornicaVirtualnihDretvi.newThread(() -> posluziteljKazni.pokreniPosluzitelja());
		testnaDretva.start();

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		assertTrue(provjeriAktivnostPorta("localhost", this.posluziteljKazni.mreznaVrata));

		testnaDretva.interrupt();
	}

	@Test
	@Order(32)
	void testMainPokreniPosluziteljaUspjesno() {
		int vrataKazne = 8020;
		try {
			Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(KONFIG_DATOTEKA);
			konfig.spremiPostavku("mreznaVrataKazne", Integer.toString(vrataKazne));
			konfig.spremiKonfiguraciju();
		} catch (NeispravnaKonfiguracija e) {
			Thread.currentThread().interrupt();
		}

		assertFalse(provjeriAktivnostPorta("localhost", vrataKazne));

		String[] argumenti = { KONFIG_DATOTEKA };
		this.posluziteljKazni.mreznaVrata = vrataKazne;
		Thread testnaDretva = tvornicaVirtualnihDretvi.newThread(() -> PosluziteljKazni.main(argumenti));
		testnaDretva.start();

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		assertTrue(provjeriAktivnostPorta("localhost", vrataKazne));

		testnaDretva.interrupt();
		obrisiDatoteku(KONFIG_DATOTEKA);
	}
}
