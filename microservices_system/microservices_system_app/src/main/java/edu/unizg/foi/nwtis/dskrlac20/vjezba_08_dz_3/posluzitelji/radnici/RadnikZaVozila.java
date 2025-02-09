package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.posluzitelji.radnici;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.klijenti.RestKlijentVozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.PodaciVozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Vozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.pomocnici.GpsUdaljenostBrzina;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.posluzitelji.PosluziteljZaVozila;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.posluzitelji.PosluziteljZaVozila.aktivniKanali;

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
    private final String restServisVozila;
    private final PosluziteljZaVozila posluziteljZaVozila;
    public AsynchronousSocketChannel klijentskiKanal;
    public PodaciVozila podaciVozila;
    public final Pattern predlozakZahtjevaVozila = Pattern
        .compile("^VOZILO (?<id>\\d+) (?<broj>\\d+) (?<vrijeme>\\d+) "
            + "(?<brzina>-?\\d+(\\.\\d+)?) (?<snaga>-?\\d+(\\.\\d+)?) (?<struja>-?\\d+(\\.\\d+)?) "
            + "(?<visina>-?\\d+(\\.\\d+)?) (?<gpsBrzina>-?\\d+(\\.\\d+)?) (?<tempVozila>\\d+) "
            + "(?<postotakBaterija>\\d+) (?<naponBaterija>-?\\d+(\\.\\d+)?) "
            + "(?<kapacitetBaterija>\\d+) (?<tempBaterija>\\d+) (?<preostaloKm>-?\\d+(\\.\\d+)?) "
            + "(?<ukupnoKm>-?\\d+(\\.\\d+)?) (?<gpsSirina>-?\\d+\\.\\d+) (?<gpsDuzina>-?\\d+\\.\\d+)\n$");
    public final Pattern predlozakZahtjevaStartStop = Pattern
        .compile("^VOZILO (START|STOP) (?<id>\\d+)\n$");
    public Matcher poklapanjeDosegaRadara;
    public Matcher poklapanjeStartStop;
    public static ConcurrentLinkedQueue<Integer> pracenaVozila = new ConcurrentLinkedQueue<>();

    /**
     * Konstruktor koji stvara instancu {@code RadnikZaVozila}. Povezuje radnika sa
     * {@code PosluziteljZaVozila} i kanalom preko kojeg će primati zahtjeve.
     *
     * @param posluziteljZaVozila Instanca poslužitelja za vozila kojem radnik
     *                            pripada.
     * @param klijentskiKanal     Asinkroni mrežni kanal kroz koji radnik komunicira
     *                            s klijentima.
     */
    public RadnikZaVozila(PosluziteljZaVozila posluziteljZaVozila,
        AsynchronousSocketChannel klijentskiKanal, String restServisVozila) {
        this.posluziteljZaVozila = posluziteljZaVozila;
        this.klijentskiKanal = klijentskiKanal;
        this.restServisVozila = restServisVozila;
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
                int procitaniBajtovi = klijentskiKanal.read(spremnik).get();
                if (procitaniBajtovi == -1) {
                    klijentskiKanal.close();
                    break;
                }
                spremnik.flip();
                String zahtjev = new String(spremnik.array(), 0, spremnik.limit());
                obradiZahtjev(zahtjev);
                spremnik.clear();
            } catch (ExecutionException | InterruptedException | IOException e) {
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
    public void obradiZahtjev(String zahtjev) throws ExecutionException, InterruptedException {
        if (zahtjev == null) {
            klijentskiKanal
                .write(ByteBuffer.wrap("ERROR 20 Neispravan format komande.".getBytes()));
            return;
        }

        poklapanjeStartStop = predlozakZahtjevaStartStop.matcher(zahtjev);
        if (poklapanjeStartStop.matches()) {
            String command = poklapanjeStartStop.group(1);
            int id = Integer.parseInt(poklapanjeStartStop.group("id"));
            if ("START".equals(command)) {
                zapocniPracenje(id);
            } else if ("STOP".equals(command)) {
                zaustaviPracenje(id);
            }
            return;
        }

        poklapanjeDosegaRadara = predlozakZahtjevaVozila.matcher(zahtjev);
        if (!poklapanjeDosegaRadara.matches()) {
            klijentskiKanal
                .write(ByteBuffer.wrap("ERROR 20 Neispravan format komande.".getBytes()));
            return;
        }

        evidentirajPodatkeVozila();

        if (pracenaVozila.contains(podaciVozila.id())) {
            var odgovor = posaljiZahtjevNaRestServis(podaciVozila);
            if (odgovor) {
                posaljiZahtjevPosluziteljuRadara();
            } else {
                klijentskiKanal.write(ByteBuffer
                    .wrap("ERROR 21 Neuspješno slanje zahtjeva na REST servis.".getBytes()));
            }
        } else {
            posaljiZahtjevPosluziteljuRadara();
        }
    }

    /**
     * Započinje praćenje vozila dodavanjem njegovog ID-a u listu praćenih vozila.
     *
     * @param idVozila ID vozila koje treba pratiti.
     */
    private void zapocniPracenje(int idVozila) {
        if (!pracenaVozila.contains(idVozila)) {
            pracenaVozila.offer(idVozila);
            klijentskiKanal.write(ByteBuffer.wrap("OK".getBytes()));
        } else {
            var odgovor = "ERROR 29 Vozilo " + idVozila + " se vec prati.";
            klijentskiKanal.write(ByteBuffer.wrap(odgovor.getBytes()));
        }
    }

    /**
     * Zaustavlja praćenje vozila uklanjanjem njegovog ID-a iz liste praćenih
     * vozila.
     *
     * @param idVozila ID vozila koje treba prestati pratiti.
     */
    private void zaustaviPracenje(int idVozila) {
        if (pracenaVozila.remove(idVozila)) {
            klijentskiKanal.write(ByteBuffer.wrap("OK".getBytes()));
        } else {
            var odgovor = "ERROR 29 Vozilo " + idVozila + " nije u pracenju.";
            klijentskiKanal.write(ByteBuffer.wrap(odgovor.getBytes()));
        }
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
        return posluziteljZaVozila.centralniSustav.registriraniRadari.values().stream()
            .filter(radar -> {
                double udaljenost = GpsUdaljenostBrzina.udaljenostKm(podaciVozila.gpsSirina(),
                    podaciVozila.gpsDuzina(), radar.gpsSirina(), radar.gpsDuzina());
                return udaljenost <= (double) radar.maksUdaljenost() / 1000;
            }).findFirst().orElse(null);
    }

    /**
     * Šalje zahtjev na REST servis za dodavanje vožnje vozila.
     *
     * @param podaciVozila Podaci o vozilu koji se šalju na REST servis.
     * @return true ako je zahtjev uspješno poslan, false inače.
     */
    private boolean posaljiZahtjevNaRestServis(PodaciVozila podaciVozila) {
        var restKlijentVozila = new RestKlijentVozila(restServisVozila);
        Vozila vozila = new Vozila(podaciVozila.id(), podaciVozila.broj(), podaciVozila.vrijeme(),
            podaciVozila.brzina(), podaciVozila.snaga(), podaciVozila.struja(),
            podaciVozila.visina(), podaciVozila.gpsBrzina(), podaciVozila.tempVozila(),
            podaciVozila.postotakBaterija(), podaciVozila.naponBaterija(),
            podaciVozila.kapacitetBaterija(), podaciVozila.tempBaterija(),
            podaciVozila.preostaloKm(), podaciVozila.ukupnoKm(), podaciVozila.gpsSirina(),
            podaciVozila.gpsDuzina());

        return restKlijentVozila.dodajVoznjuVozila(vozila);
    }

    /**
     * Šalje zahtjev poslužitelju radara s informacijama o vozilu.
     */
    public void posaljiZahtjevPosluziteljuRadara() {
        PodaciRadara radar = dohvatiRadaraZaVozilo();
        if (radar == null) {
            return;
        }

        StringBuilder komanda = new StringBuilder();
        komanda.append("VOZILO ").append(podaciVozila.id()).append(" ")
            .append(podaciVozila.vrijeme()).append(" ").append(podaciVozila.brzina())
            .append(" ").append(podaciVozila.gpsSirina()).append(" ")
            .append(podaciVozila.gpsDuzina());

        MrezneOperacije.posaljiZahtjevPosluzitelju(radar.adresaRadara(), radar.mreznaVrataRadara(),
            komanda.toString());
    }
}
