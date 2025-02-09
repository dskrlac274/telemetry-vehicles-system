package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.posluzitelji;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.posluzitelji.radnici.RadnikZaRadare;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa {@code PosluziteljRadara} predstavlja mrežni poslužitelj koji vrši
 * praćenje vozila. Također, klasa predstavlja klijenta za
 * {@code PosluziteljZaRegistracijuRadara} te omogućuje brisanje pojedinog
 * odnosno brisanje svih vozila.
 *
 * @author dskrlac20
 */
public class PosluziteljRadara {
    public PodaciRadara podaciRadara;
    private final ThreadFactory tvornicaVirtualnihDretvi = Thread.ofVirtual().factory();
    private final Pattern predlozakRegistracije = Pattern.compile("^(?<datotekaKonf>.+)$");
    private final Pattern predlozakBrisanja = Pattern
        .compile("^(?<datotekaKonf>.+) OBRIŠI (?<id>\\d+)$");
    private final Pattern predlozakBrisanjaSve = Pattern
        .compile("^(?<datotekaKonf>.+) OBRIŠI SVE$");

    /**
     * Služi za pokretanje poslužitelja radara. Procesira argumente pokretanja,
     * preuzima postavke iz konfiguracijske datoteke ili služi kao klijent
     * operacijama brisanja.
     *
     * @param args argumenti komandne linije
     */
    public static void main(String[] args) {
        PosluziteljRadara posluziteljRadara = new PosluziteljRadara();
        try {
            provjeriArgumentePokretanja(args);
            posluziteljRadara.preuzmiPostavke(args[0]);
            posluziteljRadara.obradiKomandu(args);
        } catch (NeispravnaKonfiguracija | IllegalArgumentException | IOException e) {
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
    private static void provjeriArgumentePokretanja(String[] args) throws IllegalArgumentException {
        if (args.length != 1 && args.length != 3) {
            throw new IllegalArgumentException("Neispravan format komande.");
        }
    }

    /**
     * Preuzima konfiguracijske postavke iz datoteke i postavlja
     * {@code PodaciRadara}.
     *
     * @param datotekaKonfiguracije putanja do konfiguracijske datoteke
     * @throws NeispravnaKonfiguracija ako konfiguracijska datoteka ne postoji ili
     *                                 je neispravna
     * @throws NumberFormatException   ako konfiguracijski podaci nisu u ispravnom
     *                                 formatu
     */
    public void preuzmiPostavke(String datotekaKonfiguracije)
        throws NeispravnaKonfiguracija, UnknownHostException, NumberFormatException {
        Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfiguracije);
        this.podaciRadara = new PodaciRadara(Integer.parseInt(konfig.dajPostavku("id")),
            InetAddress.getLocalHost().getHostAddress(),
            Integer.parseInt(konfig.dajPostavku("mreznaVrataRadara")),
            Integer.parseInt(konfig.dajPostavku("maksBrzina")),
            Integer.parseInt(konfig.dajPostavku("maksTrajanje")),
            Integer.parseInt(konfig.dajPostavku("maksUdaljenost")),
            konfig.dajPostavku("adresaRegistracije"),
            Integer.parseInt(konfig.dajPostavku("mreznaVrataRegistracije")),
            konfig.dajPostavku("adresaKazne"),
            Integer.parseInt(konfig.dajPostavku("mreznaVrataKazne")),
            konfig.dajPostavku("postanskaAdresaRadara"),
            Double.parseDouble(konfig.dajPostavku("gpsSirina")),
            Double.parseDouble(konfig.dajPostavku("gpsDuzina")));
    }

    /**
     * Analizira i obrađuje ulazne komande za registraciju radara, brisanje
     * pojedinačnih radara ili svih radara. Ako nema specifične komande, pokreće
     * postupak registracije i zatim pokreće mrežni poslužitelj.
     *
     * @param args argumenti komandne linije
     */
    private void obradiKomandu(String[] args) throws IllegalArgumentException {
        String zahtjev = String.join(" ", args);
        Matcher poklapanjeRegistracija = this.predlozakRegistracije.matcher(zahtjev);
        Matcher poklapanjeBrisanje = this.predlozakBrisanja.matcher(zahtjev);
        Matcher poklapanjeBrisanjeSve = this.predlozakBrisanjaSve.matcher(zahtjev);

        if (poklapanjeBrisanje.matches()) {
            int id = Integer.parseInt(poklapanjeBrisanje.group("id"));
            zahtjevObrisiRadara(id);
        } else if (poklapanjeBrisanjeSve.matches()) {
            zahtjevObrisiSveRadare();
        } else if (poklapanjeRegistracija.matches()) {
            if (registrirajSe()) {
                pokreniPosluzitelja();
            }
        } else {
            throw new IllegalArgumentException("Neispravan format komande.");
        }
    }

    /**
     * Pokreće poslužitelj koji čeka na mrežnim vratima radara, prihvaća veze od
     * klijenata i obrađuje zahtjeve kroz virtualne dretve {@code RadnikZaRadare}.
     */
    public void pokreniPosluzitelja() {
        try (ServerSocket mreznaUticnicaPosluzitelja = new ServerSocket(
            podaciRadara.mreznaVrataRadara())) {
            while (!Thread.currentThread().isInterrupted()) {
                var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
                tvornicaVirtualnihDretvi.newThread(new RadnikZaRadare(this, mreznaUticnica))
                    .start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Registrira radar kod {@code PosluziteljZaRegistracijuRadara}, šaljući
     * potrebne podatke za registraciju.
     *
     * @return Vraća true ako je registracija uspješna, inače false.
     */
    public boolean registrirajSe() {
        StringBuilder komanda = new StringBuilder();
        komanda.append("RADAR ").append(podaciRadara.id()).append(" ")
            .append(podaciRadara.adresaRadara()).append(" ")
            .append(podaciRadara.mreznaVrataRadara()).append(" ")
            .append(podaciRadara.gpsSirina()).append(" ").append(podaciRadara.gpsDuzina())
            .append(" ").append(podaciRadara.maksUdaljenost());

        String odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(
            podaciRadara.adresaRegistracije(), podaciRadara.mreznaVrataRegistracije(),
            komanda.toString());

        return odgovor != null && odgovor.startsWith("OK");
    }

    /**
     * Šalje komandu za brisanje specifičnog radara prema
     * {@code PosluziteljZaRegistracijuRadara}.
     *
     * @param id Identifikator radara koji treba obrisati.
     */
    private void zahtjevObrisiRadara(int id) {
        StringBuilder komanda = new StringBuilder();
        komanda.append("RADAR OBRIŠI ").append(id);

        MrezneOperacije.posaljiZahtjevPosluzitelju(podaciRadara.adresaRegistracije(),
            podaciRadara.mreznaVrataRegistracije(), komanda.toString());
    }

    /**
     * Šalje komandu za brisanje svih radara prema
     * {@code PosluziteljZaRegistracijuRadara}.
     */
    private void zahtjevObrisiSveRadare() {
        String komanda = "RADAR OBRIŠI SVE";
        MrezneOperacije.posaljiZahtjevPosluzitelju(podaciRadara.adresaRegistracije(),
            podaciRadara.mreznaVrataRegistracije(), komanda);
    }
}
