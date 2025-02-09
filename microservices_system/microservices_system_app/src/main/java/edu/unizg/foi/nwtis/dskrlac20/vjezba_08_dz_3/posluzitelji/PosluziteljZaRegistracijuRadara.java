package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.posluzitelji;

import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.pomocnici.MrezneOperacije;

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
    private final Pattern predlozakRegistracije = Pattern
        .compile("^RADAR (?<id>\\d+) (?<adresa>\\S+) "
            + "(?<mreznaVrata>\\d+) (?<gpsSirina>-?\\d+\\.\\d+) (?<gpsDuzina>-?\\d+\\.\\d+) "
            + "(?<maksUdaljenost>-?\\d+)$");
    private final Pattern predlozakBrisanja = Pattern.compile("^RADAR OBRIŠI (?<id>\\d+)$");
    private final Pattern predlozakBrisanjaSve = Pattern.compile("^RADAR OBRIŠI SVE$");
    private final Pattern predlozakPostojanje = Pattern.compile("^RADAR (?<id>\\d+)$");
    private final Pattern predlozakReset = Pattern.compile("^RADAR RESET$");
    private final Pattern predlozakSvi = Pattern.compile("^RADAR SVI$");
    private Matcher poklapanjeRegistracija;
    private Matcher poklapanjeBrisanje;
    private Matcher poklapanjePostojanje;

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
        try (ServerSocket mreznaUticnicaPosluzitelja = new ServerSocket(
            centralniSustav.mreznaVrataRadara)) {
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
        poklapanjePostojanje = this.predlozakPostojanje.matcher(zahtjev);
        Matcher poklapanjeBrisanjeSve = this.predlozakBrisanjaSve.matcher(zahtjev);
        Matcher poklapanjeReset = this.predlozakReset.matcher(zahtjev);
        Matcher poklapanjeSvi = this.predlozakSvi.matcher(zahtjev);

        if (poklapanjeRegistracija.matches()) {
            return obradaRegistrirajRadar();
        } else if (poklapanjeBrisanje.matches()) {
            return obradaObrisiRadar();
        } else if (poklapanjeBrisanjeSve.matches()) {
            return obradaObrisiSveRadare();
        } else if (poklapanjePostojanje.matches()) {
            return obradaProvjeraPostojanjaRadara();
        } else if (poklapanjeReset.matches()) {
            return obradaResetRadare();
        } else if (poklapanjeSvi.matches()) {
            return obradaSviRadari();
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
            Integer.parseInt(this.poklapanjeRegistracija.group("maksUdaljenost")), null, -1,
            null, -1, null, Double.parseDouble(this.poklapanjeRegistracija.group("gpsSirina")),
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

    /**
     * Procesira zahtjev za provjeru radara sa zadanim id-om. Ako radar postoji,
     * vraća "OK", inače vraća grešku.
     *
     * @return Odgovor "OK" ako radar postoji, ili poruka o grešci ako radar ne
     *         postoji.
     */
    private String obradaProvjeraPostojanjaRadara() {
        int id = Integer.parseInt(poklapanjePostojanje.group("id"));
        return centralniSustav.registriraniRadari.containsKey(id) ? "OK"
            : "ERROR 12 Radar s id-om " + id + " ne postoji.";
    }

    /**
     * Procesira zahtjev za resetiranje radara. Provjerava za svaki radar u
     * kolekciji registriranih radara da li je aktivan slanjem komande "RADAR id"
     * tom radaru. Ako radar nije aktivan, briše ga iz kolekcije registriranih
     * radara.
     *
     * @return Odgovor u formatu "OK n m", gdje je n ukupan broj radara u trenutku
     *         primanja zahtjeva, a m broj radara koji nisu bili aktivni i obrisani
     *         su iz kolekcije registriranih radara.
     */
    private String obradaResetRadare() {
        int n = centralniSustav.registriraniRadari.size();

        List<Integer> radariZaUklanjanje = centralniSustav.registriraniRadari.entrySet().stream()
            .filter(zapis -> {
                var radar = zapis.getValue();
                String komanda = "RADAR " + radar.id();
                var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(radar.adresaRadara(),
                    radar.mreznaVrataRadara(), komanda);
                return odgovor == null;
            }).map(Map.Entry::getKey).toList();

        int m = radariZaUklanjanje.size();

        radariZaUklanjanje.forEach(centralniSustav.registriraniRadari::remove);

        return "OK " + n + " " + m;
    }

    /**
     * Procesira zahtjev za dobivanje podataka o svim registriranim radarima. Ako u
     * sustavu postoje radari, priprema podatke za svaki radar i vraća u formatu "OK
     * {[id1 adresa1 mreznaVrata1 gpsSirina1 gpsDuzina1 maksUdaljenost1], ...}".
     *
     * @return Odgovor u formatu "OK {...}", koji sadrži podatke o svim
     *         registriranim radarima.
     */
    private String obradaSviRadari() {
        if (centralniSustav.registriraniRadari.isEmpty()) {
            return "OK {}";
        }

        StringBuilder odgovor = new StringBuilder("OK {");

        boolean prviZapis = true;
        for (var zapis : centralniSustav.registriraniRadari.entrySet()) {
            PodaciRadara radar = zapis.getValue();
            if (!prviZapis) {
                odgovor.append(", ");
            } else {
                prviZapis = false;
            }
            odgovor.append("[").append(radar.id()).append(" ").append(radar.adresaRadara())
                .append(" ").append(radar.mreznaVrataRadara()).append(" ")
                .append(radar.gpsSirina()).append(" ").append(radar.gpsDuzina()).append(" ")
                .append(radar.maksUdaljenost()).append("]");
        }

        odgovor.append("}");

        return odgovor.toString();
    }
}
