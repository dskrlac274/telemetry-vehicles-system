package edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.radnici;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.podaci.BrzoVozilo;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.pomocnici.GpsUdaljenostBrzina;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.PosluziteljRadara;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa {@code RadnikZaRadare} implementira sučelje {@code Runnable} i služi za
 * obradu zahtjeva koji dolaze preko mrežnih vrata povezanih na
 * {@code PosluziteljRadara}. {@code RadnikZaRadare} analizira podatke o brzini
 * i lokaciji vozila koje mu šalju klijenti, provjerava jesu li vozila unutar
 * dozvoljenih granica brzine te geografskog dosega radara, i prema potrebi
 * generira kazne.
 *
 * @author dskrlac20
 */
public class RadnikZaRadare implements Runnable {
	public static final ConcurrentHashMap<Integer, BrzoVozilo> vozila = new ConcurrentHashMap<>();
	private final PosluziteljRadara posluziteljRadara;
	private final Socket mreznaUticnica;
	private BrzoVozilo brzoVoziloUlaz;
	public final Pattern predlozakDetekcijaRadara = Pattern
			.compile("^VOZILO (?<id>\\d+) (?<vrijeme>\\d+) (?<brzina>-?\\d+(\\.\\d+)?) (?<gpsSirina>-?\\d+\\.\\d+) "
					+ "(?<gpsDuzina>-?\\d+\\.\\d+)$");
	private Matcher poklapanjeDetekcijaRadara;

	/**
	 * Konstruktor koji stvara instancu {@code RadnikZaRadare}. Povezuje radnika s
	 * određenim {@code PosluziteljRadara} i mrežnim vratima kroz koja će primati
	 * zahtjeve.
	 *
	 * @param posluziteljRadara Instanca poslužitelja radara kojem radnik pripada.
	 * @param mreznaUticnica    Mrežna vrata preko kojih radnik komunicira s
	 *                          klijentima.
	 */
	public RadnikZaRadare(PosluziteljRadara posluziteljRadara, Socket mreznaUticnica) {
		this.posluziteljRadara = posluziteljRadara;
		this.mreznaUticnica = mreznaUticnica;
	}

	/**
	 * Glavna izvršna metoda za {@code RadnikZaRadare} koja se pokreće kada se
	 * radnik pokrene kao dretva. U ovoj metodi radnik čita zahtjev koji dolazi
	 * preko mrežnih vrata, obrađuje ga, i šalje odgovor nazad klijentu. Nakon
	 * obrade, zatvara mrežna vrata.
	 */
	@Override
	public void run() {
		try {
			BufferedReader citac = new BufferedReader(
					new InputStreamReader(mreznaUticnica.getInputStream(), StandardCharsets.UTF_8));
			PrintWriter pisac = new PrintWriter(
					new OutputStreamWriter(mreznaUticnica.getOutputStream(), StandardCharsets.UTF_8), true);

			pisac.println(obradaZahtjeva(citac.readLine()));

			citac.close();
			pisac.close();
			mreznaUticnica.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Obrađuje pristigli zahtjev od klijenta. Provjerava ispravnost formata
	 * primljenog zahtjeva i ako je ispravan, obrađuje komandu za detekciju radara.
	 *
	 * @param zahtjev Zahtjeva koji je primljen od klijenta.
	 * @return Odgovor koji će biti poslan nazad klijentu, koji može biti potvrda
	 *         ili poruka o grešci.
	 */
	public String obradaZahtjeva(String zahtjev) {
		if (zahtjev == null) {
			return "ERROR 30 Neispravan format komande.";
		}

		poklapanjeDetekcijaRadara = predlozakDetekcijaRadara.matcher(zahtjev);
		if (!poklapanjeDetekcijaRadara.matches()) {
			return "ERROR 30 Neispravan format komande.";
		}

		ucitajKomandu();

		if (!unutarDosegaRadara(brzoVoziloUlaz.gpsSirina(), brzoVoziloUlaz.gpsDuzina())) {
			return "ERROR 39 Vozilo nije unutar dosega radara.";
		}

		return odrediStanjeVoznje();
	}

	/**
	 * Obrađuje primljeni zahtjev i ekstrahira potrebne informacije poput id vozila,
	 * vremena, brzine, i GPS koordinata iz primljene komande.
	 */
	public void ucitajKomandu() {
		this.brzoVoziloUlaz = new BrzoVozilo(Integer.parseInt(poklapanjeDetekcijaRadara.group("id")), -1,
				Long.parseLong(poklapanjeDetekcijaRadara.group("vrijeme")),
				Double.parseDouble(poklapanjeDetekcijaRadara.group("brzina")),
				Double.parseDouble(poklapanjeDetekcijaRadara.group("gpsSirina")),
				Double.parseDouble(poklapanjeDetekcijaRadara.group("gpsDuzina")), false);
	}

	/**
	 * Provjerava nalazi li se vozilo unutar geografskog dosega radara.
	 *
	 * @param gpsSirina Geografska širina lokacije vozila.
	 * @param gpsDuzina Geografska dužina lokacije vozila.
	 * @return Vraća true ako je vozilo unutar dozvoljenog dosega radara, inače
	 *         false.
	 */
	public boolean unutarDosegaRadara(double gpsSirina, double gpsDuzina) {
		double udaljenost = GpsUdaljenostBrzina.udaljenostKm(gpsSirina, gpsDuzina,
				posluziteljRadara.podaciRadara.gpsSirina(), posluziteljRadara.podaciRadara.gpsDuzina());
		return udaljenost <= (double) posluziteljRadara.podaciRadara.maksUdaljenost() / 1000;
	}

	/**
	 * Određuje stanje vožnje za vozilo temeljem njegove brzine i položaja. Ažurira
	 * stanje vozila i odlučuje o slanju kazne ako je potrebno.
	 *
	 * @return Odgovor koji opisuje trenutno stanje vožnje, uključujući uspješnost
	 *         obrade ili grešku.
	 */
	public String odrediStanjeVoznje() {
		String odgovor = "OK";
		boolean uPrekrsaju = brzoVoziloUlaz.brzina() > posluziteljRadara.podaciRadara.maksBrzina();
		BrzoVozilo trenutnoVozilo = brzoVoziloUlaz.postaviStatus(uPrekrsaju);

		BrzoVozilo postojeceVozilo = vozila.get(brzoVoziloUlaz.id());
		if (postojeceVozilo == null || !postojeceVozilo.status() && uPrekrsaju) {
			trenutnoVozilo = trenutnoVozilo.postaviStatus(true);
			vozila.put(brzoVoziloUlaz.id(), trenutnoVozilo);
		} else if (uPrekrsaju) {
			odgovor = pokusajGeneriratiKaznu(postojeceVozilo, trenutnoVozilo);
		} else {
			vozila.put(brzoVoziloUlaz.id(), trenutnoVozilo);
		}

		return odgovor;
	}

	/**
	 * Pokušava generirati kaznu za vozilo ako je prekršaj trajao duže od
	 * definiranog vremenskog perioda.
	 *
	 * @param pocetnoVozilo  Podaci o vozilu na početku detekcije prekršaja.
	 * @param trenutnoVozilo Trenutni podaci o vozilu nakon detekcije.
	 * @return Vraća "OK" ako nije potrebna akcija ili poruku o grešci.
	 */
	public String pokusajGeneriratiKaznu(BrzoVozilo pocetnoVozilo, BrzoVozilo trenutnoVozilo) {
		long trajanjePrekrsaja = trenutnoVozilo.vrijeme() - pocetnoVozilo.vrijeme();
		long maksTrajanje = posluziteljRadara.podaciRadara.maksTrajanje() * 1000L;
		var novoVozilo = pocetnoVozilo.postaviStatus(false);

		if (trajanjePrekrsaja > maksTrajanje && trajanjePrekrsaja <= 2 * maksTrajanje) {
			vozila.put(trenutnoVozilo.id(), novoVozilo);
			var odgovor = posaljiKaznuPosluziteljuKazni(trenutnoVozilo, novoVozilo);
			if (odgovor == null) {
				return "ERROR 31 Poslužitelj kazni nije dostupan.";
			}
		} else if (trajanjePrekrsaja > 2 * maksTrajanje) {
			vozila.put(trenutnoVozilo.id(), novoVozilo);
		}

		return "OK";
	}

	/**
	 * Šalje komandu za izdavanje kazne poslužitelju kazni koristeći mrežne
	 * operacije.
	 *
	 * @param staroVozilo Informacije o vozilu pri početku prekršaja.
	 * @param novoVozilo  Informacije o vozilu pri kraju prekršaja.
	 * @return Odgovor poslužitelja kazni o uspješnosti slanja kazne.
	 */
	public String posaljiKaznuPosluziteljuKazni(BrzoVozilo staroVozilo, BrzoVozilo novoVozilo) {
		StringBuilder komanda = new StringBuilder();
		komanda.append("VOZILO ").append(novoVozilo.id()).append(" ").append(staroVozilo.vrijeme()).append(" ")
				.append(novoVozilo.vrijeme()).append(" ").append(novoVozilo.brzina()).append(" ")
				.append(novoVozilo.gpsSirina()).append(" ").append(novoVozilo.gpsDuzina()).append(" ")
				.append(posluziteljRadara.podaciRadara.gpsSirina()).append(" ")
				.append(posluziteljRadara.podaciRadara.gpsDuzina());

		return MrezneOperacije.posaljiZahtjevPosluzitelju(posluziteljRadara.podaciRadara.adresaKazne(),
				posluziteljRadara.podaciRadara.mreznaVrataKazne(), komanda.toString());
	}
}
