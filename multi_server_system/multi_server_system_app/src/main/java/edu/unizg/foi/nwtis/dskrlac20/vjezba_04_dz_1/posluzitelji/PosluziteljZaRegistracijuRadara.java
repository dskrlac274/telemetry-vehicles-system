package edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.podaci.PodaciRadara;

import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa {@code PosluziteljZaRegistracijuRadara} implementira sučelje
 * {@code Runnable} i služi kao poslužitelj za registraciju i upravljanje
 * radarima u centralnom sustavu. Ova klasa upravlja mrežnim zahtjevima za
 * registraciju, brisanje pojedinačnih radara ili svih radara.
 *
 * @author dskrlac20
 */
public class PosluziteljZaRegistracijuRadara implements Runnable {
	private final CentralniSustav centralniSustav;
	private final Pattern predlozakRegistracije = Pattern.compile("^RADAR (?<id>\\d+) (?<adresa>\\S+) "
			+ "(?<mreznaVrata>\\d+) (?<gpsSirina>-?\\d+\\.\\d+) (?<gpsDuzina>-?\\d+\\.\\d+) (?<maksUdaljenost>-?\\d+)$");
	private final Pattern predlozakBrisanja = Pattern.compile("^RADAR OBRIŠI (?<id>\\d+)$");
	private final Pattern predlozakBrisanjaSve = Pattern.compile("^RADAR OBRIŠI SVE$");
	private Matcher poklapanjeRegistracija;
	private Matcher poklapanjeBrisanje;

	/**
	 * Konstruktor klase {@code PosluziteljZaRegistracijuRadara} koji prima
	 * referencu na centralni sustav. Ovaj konstruktor omogućuje pristup zajedničkim
	 * resursima i postavkama definiranim u centralnom sustavu.
	 *
	 * @param centralniSustav Instanca centralnog sustava.
	 */
	public PosluziteljZaRegistracijuRadara(CentralniSustav centralniSustav) {
		this.centralniSustav = centralniSustav;
	}

	/**
	 * Glavna izvršna metoda za dretvu. Upravlja vezama zahtjeva za registraciju i
	 * brisanje radara. Za svaki zahtjev otvara se nova veza, čita se zahtjev,
	 * obrađuje i šalje odgovor, nakon čega se veza zatvara.
	 */
	@Override
	public void run() {
		try (ServerSocket mreznaUticnicaPosluzitelja = new ServerSocket(centralniSustav.mreznaVrataRadara)) {
			while (!Thread.currentThread().isInterrupted()) {
				var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
				BufferedReader citac = new BufferedReader(
						new InputStreamReader(mreznaUticnica.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter pisac = new PrintWriter(
						new OutputStreamWriter(mreznaUticnica.getOutputStream(), StandardCharsets.UTF_8), true);

				pisac.println(obradiZahtjev(citac.readLine()));

				pisac.close();
				citac.close();
				mreznaUticnica.close();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Obrađuje zahtjev koji je primljen preko mrežne. Podržava različite komande,
	 * registracija i brisanje radara. Vraća odgovor koji se zatim šalje klijentu
	 * preko mrežne.
	 *
	 * @param zahtjev Tekst zahtjeva primljenog preko mrežne veze.
	 * @return Odgovor koji se šalje natrag klijentu.
	 */
	private String obradiZahtjev(String zahtjev) {
		if (zahtjev == null) {
			return "ERROR 10 Neispravan format komande.";
		}

		poklapanjeRegistracija = this.predlozakRegistracije.matcher(zahtjev);
		poklapanjeBrisanje = this.predlozakBrisanja.matcher(zahtjev);
		Matcher poklapanjeBrisanjeSve = this.predlozakBrisanjaSve.matcher(zahtjev);

		if (poklapanjeRegistracija.matches()) {
			return obradaRegistrirajRadar();
		} else if (poklapanjeBrisanje.matches()) {
			return obradaObrisiRadar();
		} else if (poklapanjeBrisanjeSve.matches()) {
			return obradaObrisiSveRadare();
		} else {
			return "ERROR 10 Neispravan format komande.";
		}
	}

	/**
	 * Procesira zahtjev za registraciju novog radara. Provjerava postoji li radar s
	 * istim id-om i ako ne, registrira novi radar u centralnom sustavu.
	 *
	 * @return Odgovor o uspjehu registracije ili o grešci ako radar već postoji.
	 */
	private String obradaRegistrirajRadar() {
		int id = Integer.parseInt(poklapanjeRegistracija.group("id"));
		if (centralniSustav.registriraniRadari.containsKey(id)) {
			return "ERROR 11 Radar s id-om " + id + " vec je registriran.";
		}

		PodaciRadara radar = new PodaciRadara(id, this.poklapanjeRegistracija.group("adresa"),
				Integer.parseInt(this.poklapanjeRegistracija.group("mreznaVrata")), -1, -1,
				Integer.parseInt(this.poklapanjeRegistracija.group("maksUdaljenost")), null,
				-1, null, -1, null,
				Double.parseDouble(this.poklapanjeRegistracija.group("gpsSirina")),
				Double.parseDouble(this.poklapanjeRegistracija.group("gpsDuzina")));

		centralniSustav.registriraniRadari.put(id, radar);

		return "OK";
	}

	/**
	 * Procesira zahtjev za brisanje specifičnog radara na temelju id-a. Provjerava
	 * postoji li radar i ako da, briše ga iz centralnog sustava.
	 *
	 * @return Odgovor "OK" ako je radar uspješno obrisan, ili poruka o grešci ako
	 *         radar ne postoji.
	 */
	private String obradaObrisiRadar() {
		int id = Integer.parseInt(poklapanjeBrisanje.group("id"));
		PodaciRadara radar = centralniSustav.registriraniRadari.get(id);
		if (radar == null) {
			return "ERROR 12 Radar s id-om " + id + " ne postoji.";
		}

		centralniSustav.registriraniRadari.remove(id);

		return "OK";
	}

	/**
	 * Procesira zahtjev za brisanje svih radara registriranih u sustavu. Ako u
	 * sustavu postoje radari, svi se brišu, inače se vraća greška.
	 *
	 * @return Odgovor "OK" ako su svi radari obrisani, ili poruka o grešci ako ne
	 *         postoji ni jedan radar.
	 */
	private String obradaObrisiSveRadare() {
		if (centralniSustav.registriraniRadari.isEmpty()) {
			return "ERROR 19 Ne postoji niti jedan registirirani radar.";
		}

		centralniSustav.registriraniRadari.clear();

		return "OK";
	}
}
