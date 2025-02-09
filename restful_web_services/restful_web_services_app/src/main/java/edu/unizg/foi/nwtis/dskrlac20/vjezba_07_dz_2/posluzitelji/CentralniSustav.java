package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.posluzitelji;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.RedPodaciVozila;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

/**
 * Klasa {@code CentralniSustav} predstavlja središnji sustav. Klasa upravlja
 * pokretanjem {@code PosluziteljZaVozila} i
 * {@code PosluziteljZaRegistracijuRadara}. Također, sadrži popis registriranih
 * {@code PosluziteljRadara}.
 *
 * @author dskrlac20
 */
public class CentralniSustav {
	public int mreznaVrataRadara;
	public int mreznaVrataVozila;
	public int mreznaVrataNadzora;
	public int maksVozila;
	public final ThreadFactory tvornicaDretvi = Thread.ofVirtual().factory();
	public ConcurrentHashMap<Integer, PodaciRadara> registriraniRadari = new ConcurrentHashMap<>();
	public ConcurrentHashMap<Integer, RedPodaciVozila> podaciOVozilima = new ConcurrentHashMap<>();

	/**
	 * Služi za pokretanje centralnog sustava. Procesira argumente pokretanja,
	 * preuzima postavke iz konfiguracijske datoteke i pokreće poslužitelje
	 * {@code PosluziteljZaVozila} i {@code PosluziteljZaRegistracijuRadara}.
	 *
	 * @param args argumenti komandne linije
	 */
	public static void main(String[] args) {
		CentralniSustav centralniSustav = new CentralniSustav();
		try {
			provjeriArgumentePokretanja(args);
			centralniSustav.preuzmiPostavke(args[0]);
			centralniSustav.pokreniPosluzitelje();
		} catch (NeispravnaKonfiguracija | IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Provjerava ispravnost formata ulaznih argumenta potrebnih za pokretanje
	 * aplikacije.
	 *
	 * @param args argumenti komandne linije
	 * @throws IllegalArgumentException ako format nije odgovarajući
	 */
	public static void provjeriArgumentePokretanja(String[] args) throws IllegalArgumentException {
		if (args.length != 1) {
			throw new IllegalArgumentException("Neispravan format komande.");
		}
	}

	/**
	 * Preuzima konfiguracijske postavke iz datoteke i postavlja varijable.
	 *
	 * @param datotekaKonfiguracije putanja do konfiguracijske datoteke
	 * @throws NeispravnaKonfiguracija ako konfiguracijska datoteka ne postoji ili
	 *                                 je neispravna
	 * @throws NumberFormatException   ako konfiguracijski podaci nisu u ispravnom
	 *                                 formatu
	 */
	public void preuzmiPostavke(String datotekaKonfiguracije)
			throws NeispravnaKonfiguracija, NumberFormatException {
		Konfiguracija konfiguracija = KonfiguracijaApstraktna
				.preuzmiKonfiguraciju(datotekaKonfiguracije);
		this.mreznaVrataRadara = Integer.parseInt(konfiguracija.dajPostavku("mreznaVrataRadara"));
		this.mreznaVrataVozila = Integer.parseInt(konfiguracija.dajPostavku("mreznaVrataVozila"));
		this.mreznaVrataNadzora = Integer.parseInt(konfiguracija.dajPostavku("mreznaVrataNadzora"));
		this.maksVozila = Integer.parseInt(konfiguracija.dajPostavku("maksVozila"));
	}

	/**
	 * Pokreće dretve za poslužitelje koji upravljaju registracijom radara i
	 * praćenjem vozila. Dretve se pokreću preko tvornice virtualnih dretvi.
	 */
	public void pokreniPosluzitelje() {
		PosluziteljZaRegistracijuRadara posluziteljZaRegistracijuRadara = new PosluziteljZaRegistracijuRadara(
				this);
		PosluziteljZaVozila posluziteljZaVozila = new PosluziteljZaVozila(this);

		Thread posluziteljZaRegistracijuRadarDretva = tvornicaDretvi
				.newThread(posluziteljZaRegistracijuRadara);
		Thread posluziteljZaVozilaDretva = tvornicaDretvi.newThread(posluziteljZaVozila);

		posluziteljZaRegistracijuRadarDretva.start();
		posluziteljZaVozilaDretva.start();

		try {
			posluziteljZaRegistracijuRadarDretva.join();
			posluziteljZaVozilaDretva.join();
		} catch (InterruptedException e) {
			posluziteljZaVozilaDretva.interrupt();
			posluziteljZaRegistracijuRadarDretva.interrupt();
		}
	}
}
