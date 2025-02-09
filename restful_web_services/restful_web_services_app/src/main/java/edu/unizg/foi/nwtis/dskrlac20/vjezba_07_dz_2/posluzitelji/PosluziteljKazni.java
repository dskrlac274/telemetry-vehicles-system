package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.posluzitelji;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.klijenti.RestKlijentKazne;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.Kazna;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci.PodaciKazne;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import jakarta.ws.rs.ProcessingException;

import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa {@code PosluziteljKazni} služi kao mrežni poslužitelj koji obrađuje
 * zahtjeve za registraciju kazni, detalje o kaznama i statistiku kazni.
 *
 * @author dskrlac20
 */
public class PosluziteljKazni {
	public int mreznaVrata;
	public final Pattern predlozakKazna = Pattern
			.compile("^VOZILO (?<id>\\d+) (?<vrijemePocetak>\\d+) "
					+ "(?<vrijemeKraj>\\d+) (?<brzina>-?\\d+(\\.\\d+)?) (?<gpsSirina>-?\\d+\\.\\d+) "
					+ "(?<gpsDuzina>-?\\d+\\.\\d+) "
					+ "(?<gpsSirinaRadar>-?\\d+\\.\\d+) (?<gpsDuzinaRadar>-?\\d+\\.\\d+)$");
	public final Pattern predlozakDetaljiKazne = Pattern
			.compile("^VOZILO (?<id>\\d+) (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");
	public final Pattern predlozakStatistika = Pattern
			.compile("^STATISTIKA (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");
	public final Pattern predlozakTest = Pattern.compile("^TEST$");
	public Matcher poklapanjeKazna;
	public Matcher poklapanjeDetaljiKazne;
	public Matcher poklapanjeStatistika;
	public Matcher poklapanjeTest;
	public final Queue<PodaciKazne> sveKazne = new ConcurrentLinkedQueue<>();

	/**
	 * Služi za pokretanje poslužitelja kazni. Procesira argumente pokretanja,
	 * preuzima postavke iz konfiguracijske datoteke i pokreće poslužitelj za obradu
	 * zahtjeva.
	 *
	 * @param args argumenti komandne linije
	 */
	public static void main(String[] args) {
		PosluziteljKazni posluziteljKazni = new PosluziteljKazni();
		try {
			provjeriArgumentePokretanja(args);
			posluziteljKazni.preuzmiPostavke(args[0]);
			posluziteljKazni.pokreniPosluzitelja();

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
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfiguracije);
		this.mreznaVrata = Integer.parseInt(konfig.dajPostavku("mreznaVrataKazne"));
	}

	/**
	 * Pokreće poslužitelj koji sluša na zadanim mrežnim vratima.
	 */
	public void pokreniPosluzitelja() {
		try (ServerSocket mreznaUticnicaPosluzitelja = new ServerSocket(this.mreznaVrata)) {
			while (!Thread.currentThread().isInterrupted()) {
				var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
				BufferedReader citac = new BufferedReader(new InputStreamReader(
						mreznaUticnica.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter pisac = new PrintWriter(new OutputStreamWriter(
						mreznaUticnica.getOutputStream(), StandardCharsets.UTF_8), true);

				pisac.println(obradiZahtjev(citac.readLine()));

				pisac.close();
				citac.close();
				mreznaUticnica.close();
			}
		} catch (NumberFormatException | IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Obrađuje primljeni zahtjev analiziranjem njegovog sadržaja prema definiranim
	 * regex uzorcima. Ovisno o tipu zahtjeva, pokreće odgovarajuću metodu za obradu
	 * kazne, detalja kazne, statistike ili testa.
	 *
	 * @param zahtjev Tekst zahtjeva primljenog od klijenta.
	 * @return Odgovor koji se šalje klijentu kao rezultat obrade.
	 */
	public String obradiZahtjev(String zahtjev) {
		if (zahtjev == null) {
			return "ERROR 40 Neispravan format komande.";
		}

		poklapanjeKazna = this.predlozakKazna.matcher(zahtjev);
		poklapanjeDetaljiKazne = this.predlozakDetaljiKazne.matcher(zahtjev);
		poklapanjeStatistika = this.predlozakStatistika.matcher(zahtjev);
		poklapanjeTest = this.predlozakTest.matcher(zahtjev);

		if (poklapanjeKazna.matches()) {
			return obradaKazna();
		} else if (poklapanjeDetaljiKazne.matches()) {
			return obradaDetaljiKazne();
		} else if (poklapanjeStatistika.matches()) {
			return obradaStatistike();
		} else if (poklapanjeTest.matches()) {
			return obradaTest();
		} else {
			return "ERROR 40 Neispravan format komande.";
		}
	}

	/**
	 * Obrađuje zahtjev za registraciju kazne, kreirajući novi zapis o kazni i
	 * dodaje ga u red kazni.
	 *
	 * @return Odgovor koji potvrđuje uspješnu obradu.
	 */
	public String obradaKazna() {
		SimpleDateFormat jdu = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

		PodaciKazne kazna = new PodaciKazne(Integer.parseInt(poklapanjeKazna.group("id")),
				Long.parseLong(poklapanjeKazna.group("vrijemePocetak")),
				Long.parseLong(poklapanjeKazna.group("vrijemeKraj")),
				Double.parseDouble(poklapanjeKazna.group("brzina")),
				Double.parseDouble(poklapanjeKazna.group("gpsSirina")),
				Double.parseDouble(poklapanjeKazna.group("gpsDuzina")),
				Double.parseDouble(poklapanjeKazna.group("gpsSirinaRadar")),
				Double.parseDouble(poklapanjeKazna.group("gpsDuzinaRadar")));

		this.sveKazne.add(kazna);
		System.out
				.println("Id: " + kazna.id() + " Vrijeme od: " + jdu.format(kazna.vrijemePocetak())
						+ " Vrijeme do: " + jdu.format(kazna.vrijemeKraj()) + " Brzina: "
						+ kazna.brzina() + " GPS: " + kazna.gpsSirina() + ", " + kazna.gpsDuzina());

		var odgovor = posaljiZahtjevNaRestServis(kazna);

		if (!odgovor) {
			return "ERROR 42 Neuspjesno slanje zahtjeva na REST servis.";
		}

		return "OK";
	}

	/**
	 * Vraća detalje o najnovijoj kazni za zadani id vozila i vremenski interval.
	 *
	 * @return Detalji o kazni ili poruka o grešci ako kazna nije pronađena.
	 */
	public String obradaDetaljiKazne() {
		PodaciKazne najnovijaKazna = dohvatiPodatkeKazne();

		if (najnovijaKazna != null) {
			StringBuilder komanda = new StringBuilder();
			komanda.append("OK ").append(najnovijaKazna.vrijemeKraj()).append(" ")
					.append(najnovijaKazna.brzina()).append(" ")
					.append(najnovijaKazna.gpsSirinaRadar()).append(" ")
					.append(najnovijaKazna.gpsDuzinaRadar());

			return komanda.toString();
		} else {
			return "ERROR 41 Nema kazne za zadano vozilo u zadanom periodu.";
		}
	}

	/**
	 * Dohvaća detalje o najnovijoj kazni za zadano vozilo unutar određenog
	 * vremenskog intervala.
	 *
	 * @return Najnovija {@code PodaciKazne} za zadani id vozila i vremenski
	 *         interval, ili {@code null} ako kazna ne postoji.
	 */
	public PodaciKazne dohvatiPodatkeKazne() {
		int idVozila = Integer.parseInt(poklapanjeDetaljiKazne.group("id"));
		long vrijemeOd = Long.parseLong(poklapanjeDetaljiKazne.group("vrijemeOd"));
		long vrijemeDo = Long.parseLong(poklapanjeDetaljiKazne.group("vrijemeDo"));

		PodaciKazne najnovijaKazna = null;

		for (PodaciKazne kazna : sveKazne) {
			if (kazna.id() == idVozila && kazna.vrijemePocetak() >= vrijemeOd
					&& kazna.vrijemeKraj() <= vrijemeDo) {
				if (najnovijaKazna == null
						|| kazna.vrijemePocetak() > najnovijaKazna.vrijemePocetak()) {
					najnovijaKazna = kazna;
				}
			}
		}
		return najnovijaKazna;
	}

	/**
	 * Generira statistiku kazni za zadani vremenski interval, vraćajući broj kazni
	 * po vozilu.
	 *
	 * @return Statistika kazni ili poruka o grešci ako nema kazni u zadanom
	 *         intervalu.
	 */
	public String obradaStatistike() {
		var statistikaKazni = dohvatiStatistikuKazni();

		if (statistikaKazni.isEmpty()) {
			return "ERROR 49 Nema definiranih vozila u zadanom intervalu.";
		}

		StringBuilder odgovor = new StringBuilder("OK");
		for (Integer idVozila : statistikaKazni.keySet()) {
			odgovor.append(String.format(" %d %d;", idVozila, statistikaKazni.get(idVozila)));
		}

		return odgovor.toString();
	}

	/**
	 * Izračunava statistiku kazni za sva vozila unutar specificiranog vremenskog
	 * intervala.
	 *
	 * @return Mapa koja sadrži identifikatore vozila i pripadajući broj kazni
	 *         unutar zadanog intervala.
	 */
	public Map<Integer, Integer> dohvatiStatistikuKazni() {
		long vrijemeOd = Long.parseLong(poklapanjeStatistika.group("vrijemeOd"));
		long vrijemeDo = Long.parseLong(poklapanjeStatistika.group("vrijemeDo"));

		Map<Integer, Integer> statistikaKazni = new HashMap<>();

		sveKazne.forEach(kazna -> statistikaKazni.put(kazna.id(), 0));

		for (PodaciKazne kazna : sveKazne) {
			if (kazna.vrijemePocetak() >= vrijemeOd && kazna.vrijemeKraj() <= vrijemeDo) {
				statistikaKazni.put(kazna.id(), statistikaKazni.get(kazna.id()) + 1);
			}
		}
		return statistikaKazni;
	}

	/**
	 * Obrađuje zahtjev za testiranje veze i vraća odgovor "OK" ako je veza
	 * ispravna.
	 *
	 * @return Odgovor "OK".
	 */
	public String obradaTest() {
		return "OK";
	}

	/**
	 * Šalje zahtjev na REST servis za dodavanje kazne.
	 *
	 * @param podaciKazne Podaci o kazni koji se šalju na REST servis.
	 * @return true ako je zahtjev uspješno poslan, false inače.
	 */
	private boolean posaljiZahtjevNaRestServis(PodaciKazne podaciKazne) {
		var restKlijentKazne = new RestKlijentKazne();
		Kazna kazna = new Kazna(podaciKazne.id(), podaciKazne.vrijemePocetak(),
				podaciKazne.vrijemeKraj(), podaciKazne.brzina(), podaciKazne.gpsSirina(),
				podaciKazne.gpsDuzina(), podaciKazne.gpsSirinaRadar(),
				podaciKazne.gpsDuzinaRadar());
		try {
			return restKlijentKazne.dodajKaznu(kazna);
		}catch(ProcessingException e) {
			return false;
		}
	}
}
