package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.klijenti;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa {@code Klijent} služi za komunikaciju s poslužiteljem putem mrežnih
 * operacija. Svrha komunikacije za poslužiteljem leži u dohvaćanju kazni
 * određenog vozila ili svih vozila. Ukoliko je rijec o jednom vozilu dohvaća
 * najsvježiju kaznu u zadanom intervalu. Ukoliko je rijec o više vozila dohvaća
 * broj kazni za određena vozila u zadanom intervalu. Klasa koristi
 * konfiguracijske datoteke za postavljanje potrebnih parametara i provjerava
 * ispravnost ulaznih komandi pri pokretanju.
 *
 * @author dskrlac20
 */
public class Klijent {
	private String adresaKazne;
	private int mreznaVrataKazne;
	private final Pattern predlozakKaznaVozila = Pattern
			.compile("^(?<datotekaKonf>.+) (?<id>\\d+) (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");
	private final Pattern predlozakKaznaSvihVozila = Pattern
			.compile("^(?<datotekaKonf>.+) (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");

	/**
	 * Služi za pokretanje aplikacije {@code Klijent}. Provjerava ulazne argumente,
	 * preuzima postavke i šalje zahtjeve poslužitelju.
	 *
	 * @param args argumenti komandne linije
	 */
	public static void main(String[] args) {
		Klijent klijent = new Klijent();
		try {
			provjeriArgumentePokretanja(args);
			klijent.preuzmiPostavke(args[0]);
			String komanda = klijent.provjeriIUcitajKomanduPokretanja(args);
			MrezneOperacije.posaljiZahtjevPosluzitelju(klijent.adresaKazne,
					klijent.mreznaVrataKazne, komanda);
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
		if (args.length != 3 && args.length != 4) {
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
	private void preuzmiPostavke(String datotekaKonfiguracije)
			throws NeispravnaKonfiguracija, NumberFormatException {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfiguracije);
		adresaKazne = konfig.dajPostavku("adresaKazne");
		mreznaVrataKazne = Integer.parseInt(konfig.dajPostavku("mreznaVrataKazne"));
	}

	/**
	 * Provjerava i učitava komandu za pokretanje na temelju ulaznih parametara.
	 *
	 * @param args ulazni argumenti komandne linije
	 * @return formatirana komanda za slanje poslužitelju
	 * @throws IllegalArgumentException ako format komande nije ispravan
	 */
	private String provjeriIUcitajKomanduPokretanja(String[] args) throws IllegalArgumentException {
		String ulazniArgumenti = String.join(" ", args);
		Matcher preklapanjeVozila = predlozakKaznaVozila.matcher(ulazniArgumenti);
		Matcher preklapanjeSvihVozila = predlozakKaznaSvihVozila.matcher(ulazniArgumenti);

		StringBuilder komanda = new StringBuilder();
		if (preklapanjeVozila.matches()) {
			komanda.append("VOZILO ").append(preklapanjeVozila.group("id")).append(" ")
					.append(preklapanjeVozila.group("vrijemeOd")).append(" ")
					.append(preklapanjeVozila.group("vrijemeDo"));
			return komanda.toString();
		} else if (preklapanjeSvihVozila.matches()) {
			komanda.append("STATISTIKA ").append(preklapanjeSvihVozila.group("vrijemeOd"))
					.append(" ").append(preklapanjeSvihVozila.group("vrijemeDo"));
			return komanda.toString();
		} else {
			throw new IllegalArgumentException("Neispravan format komande.");
		}
	}
}
