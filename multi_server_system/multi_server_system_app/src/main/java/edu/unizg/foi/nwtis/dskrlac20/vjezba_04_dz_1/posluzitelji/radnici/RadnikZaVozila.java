package edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.radnici;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.podaci.PodaciVozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.pomocnici.GpsUdaljenostBrzina;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.PosluziteljZaVozila;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.posluzitelji.PosluziteljZaVozila.aktivniKanali;

/**
 * Klasa {@code RadnikZaVozila} implementira sučelje {@code Runnable} i služi za
 * obradu zahtjeva koji dolaze od klijenata preko asinkronih mrežnih kanala.
 * {@code RadnikZaVozila} analizira podatke o vozilima, validira ih prema
 * predloženom uzorku, evidentira i obrađuje te podatke, te komunicira s
 * poslužiteljem radara ako je potrebno.
 *
 * @author dskrlac20
 */
public class RadnikZaVozila implements Runnable {
	private final PosluziteljZaVozila posluziteljZaVozila;
	public AsynchronousSocketChannel klijentskiKanal;
	public PodaciVozila podaciVozila;
	public final Pattern predlozakZahtjevaVozila = Pattern
			.compile("^VOZILO (?<id>\\d+) (?<broj>\\d+) (?<vrijeme>\\d+) (?<brzina>-?\\d+(\\.\\d+)?) "
					+ "(?<snaga>-?\\d+(\\.\\d+)?) (?<struja>-?\\d+(\\.\\d+)?) (?<visina>-?\\d+(\\.\\d+)?) "
					+ "(?<gpsBrzina>\\d+\\.?\\d*) (?<tempVozila>\\d+) (?<postotakBaterija>\\d+) "
					+ "(?<naponBaterija>-?\\d+(\\.\\d+)?) (?<kapacitetBaterija>\\d+) (?<tempBaterija>\\d+) "
					+ "(?<preostaloKm>-?\\d+(\\.\\d+)?) (?<ukupnoKm>-?\\d+(\\.\\d+)?) (?<gpsSirina>-?\\d+\\.\\d+) "
					+ "(?<gpsDuzina>-?\\d+\\.\\d+)$");
	public Matcher poklapanjeDosegaRadara;

	/**
	 * Konstruktor koji stvara instancu {@code RadnikZaVozila}. Povezuje radnika sa
	 * {@code PosluziteljZaVozila} i kanalom preko kojeg će primati zahtjeve.
	 *
	 * @param posluziteljZaVozila Instanca poslužitelja za vozila kojem radnik
	 *                            pripada.
	 * @param klijentskiKanal     Asinkroni mrežni kanal kroz koji radnik komunicira
	 *                            s klijentima.
	 */
	public RadnikZaVozila(PosluziteljZaVozila posluziteljZaVozila, AsynchronousSocketChannel klijentskiKanal) {
		this.posluziteljZaVozila = posluziteljZaVozila;
		this.klijentskiKanal = klijentskiKanal;
	}

	/**
	 * Glavna izvršna metoda za {@code RadnikZaVozila} koja se pokreće kada se
	 * radnik pokrene kao dretva. Sluša za dolazne zahtjeve preko * asinkronog
	 * kanala, obrađuje ih i šalje odgovore. Metoda se vrti u petlji sve dok je *
	 * mrežni kanal otvoren i prekida se u slučaju iznimki.
	 */
	@Override
	public void run() {
		while (klijentskiKanal.isOpen()) {
			ByteBuffer spremnik = ByteBuffer.allocate(1024);
			try {
				klijentskiKanal.read(spremnik).get();
				spremnik.flip();
				String zahtjev = new String(spremnik.array(), 0, spremnik.limit());
				obradiZahtjev(zahtjev);
				spremnik.clear();
			} catch (ExecutionException | InterruptedException e) {
				aktivniKanali.remove(klijentskiKanal);
				return;
			}
		}
		aktivniKanali.remove(klijentskiKanal);
	}

	/**
	 * Obrađuje zahtjev primljen od klijenta. Provjerava ispravnost formata
	 * primljenog zahtjeva. Ako je format neispravan, šalje odgovor o grešci. Ako je
	 * ispravan, obrađuje podatke i upravlja daljnjim postupcima za registraciju ili
	 * komunikaciju s radarom.
	 *
	 * @param zahtjev Tekst zahtjeva primljenog od klijenta.
	 */
	public void obradiZahtjev(String zahtjev) {
		if (zahtjev == null) {
			klijentskiKanal.write(ByteBuffer.wrap("ERROR 20 Neispravan format komande.".getBytes()));
			return;
		}

		poklapanjeDosegaRadara = predlozakZahtjevaVozila.matcher(zahtjev);
		if (!poklapanjeDosegaRadara.matches()) {
			klijentskiKanal.write(ByteBuffer.wrap("ERROR 20 Neispravan format komande.".getBytes()));
			return;
		}

		evidentirajPodatkeVozila();
		PodaciRadara radar = dohvatiRadaraZaVozilo();
		if (radar == null) {
			return;
		}

		posaljiZahtjevPosluziteljuRadara(radar);
	}

	/**
	 * Parsira i evidentira podatke vozila iz zahtjeva i stvara novi objekt klase
	 * {@code PodaciVozila}.
	 */
	public void evidentirajPodatkeVozila() {
		podaciVozila = new PodaciVozila(Integer.parseInt(poklapanjeDosegaRadara.group("id")),
				Integer.parseInt(poklapanjeDosegaRadara.group("broj")),
				Long.parseLong(poklapanjeDosegaRadara.group("vrijeme")),
				Double.parseDouble(poklapanjeDosegaRadara.group("brzina")),
				Double.parseDouble(poklapanjeDosegaRadara.group("snaga")),
				Double.parseDouble(poklapanjeDosegaRadara.group("struja")),
				Double.parseDouble(poklapanjeDosegaRadara.group("visina")),
				Double.parseDouble(poklapanjeDosegaRadara.group("gpsBrzina")),
				Integer.parseInt(poklapanjeDosegaRadara.group("tempVozila")),
				Integer.parseInt(poklapanjeDosegaRadara.group("postotakBaterija")),
				Double.parseDouble(poklapanjeDosegaRadara.group("naponBaterija")),
				Integer.parseInt(poklapanjeDosegaRadara.group("kapacitetBaterija")),
				Integer.parseInt(poklapanjeDosegaRadara.group("tempBaterija")),
				Double.parseDouble(poklapanjeDosegaRadara.group("preostaloKm")),
				Double.parseDouble(poklapanjeDosegaRadara.group("ukupnoKm")),
				Double.parseDouble(poklapanjeDosegaRadara.group("gpsSirina")),
				Double.parseDouble(poklapanjeDosegaRadara.group("gpsDuzina")));
	}

	/**
	 * Određuje radar u čijem je dometu vozilo simulacije na temelju GPS koordinata
	 * vozila i radara. Vraća objekt {@code PodaciRadara}, ili {@code null} ako
	 * vozilo nije u dosegu niti jednog radara.
	 *
	 * @return Radar ili null ako radar nije pronađen.
	 */
	public PodaciRadara dohvatiRadaraZaVozilo() {
		return posluziteljZaVozila.centralniSustav.registriraniRadari.values().stream().filter(radar -> {
			double udaljenost = GpsUdaljenostBrzina.udaljenostKm(podaciVozila.gpsSirina(), podaciVozila.gpsDuzina(),
					radar.gpsSirina(), radar.gpsDuzina());
			return udaljenost <= (double) radar.maksUdaljenost() / 1000;
		}).findFirst().orElse(null);
	}

	/**
	 * Šalje zahtjev poslužitelju radara s informacijama o vozilu.
	 *
	 * @param radar Podaci o radaru kojem se šalje zahtjev.
	 * @return Odgovor od poslužitelja radara na poslani zahtjev.
	 */
	public String posaljiZahtjevPosluziteljuRadara(PodaciRadara radar) {
		StringBuilder komanda = new StringBuilder();
		komanda.append("VOZILO ").append(podaciVozila.id()).append(" ").append(podaciVozila.vrijeme()).append(" ")
				.append(podaciVozila.brzina()).append(" ").append(podaciVozila.gpsSirina()).append(" ")
				.append(podaciVozila.gpsDuzina());

		return MrezneOperacije.posaljiZahtjevPosluzitelju(radar.adresaRadara(), radar.mreznaVrataRadara(),
				komanda.toString());
	}
}
