package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.klijenti;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa {@code SimulacijaVozila} simulira rad vozila na temelju podataka iz CSV
 * datoteke. Klasa se povezuje na poslužitelj putem asinkronog mrežnog kanala i
 * šalje komande bazirane poslužitelju.
 *
 * @author dskrlac20
 */
public class SimulatorVozila {
    private String adresaVozila;
    private int mreznaVrataVozila;
    private int trajanjeSek;
    private int trajanjePauze;
    private int redniBroj = 1;
    private long prethodnoVrijeme = 0;
    private AsynchronousSocketChannel klijentskiKanal;
    private final Pattern predlozakPokretanja = Pattern
        .compile("^(?<datotekaKonf>.+) (?<datotekaSimulacije>.+\\.csv) (?<id>\\d+)$");
    private Matcher preklapanjePokretanja;

    /**
     * Služi za pokretanje aplikacije {@code SimulacijaVozila}. Provjerava ulazne
     * argumente, preuzima postavke i pokreće simulaciju..
     *
     * @param args argumenti komandne linije
     */
    public static void main(String[] args) {
        SimulatorVozila simulatorVozila = new SimulatorVozila();
        try {
            provjeriArgumentePokretanja(args);
            simulatorVozila.preuzmiPostavke(args[0]);
            simulatorVozila.pokreniSimulaciju(args);
        } catch (NeispravnaKonfiguracija | IllegalArgumentException | IOException
            | InterruptedException | ExecutionException e) {
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
        if (args.length != 3) {
            throw new IllegalArgumentException("Neispravan format komande.");
        }
    }

    /**
     * Pokreće simulaciju rada vozila čitanjem i obradom podataka iz CSV datoteke
     * specificirane u argumentima.
     *
     * @param args argumenti komandne linije
     * @throws IOException             ako dođe do problema s mrežnim operacijama
     * @throws ExecutionException      ako asinkrona operacija ne uspije
     * @throws NeispravnaKonfiguracija ako postavke iz datoteke nisu ispravne
     * @throws InterruptedException    ako dretva bude prekinuta tijekom pauze
     */
    private void pokreniSimulaciju(String[] args)
        throws IOException, ExecutionException, NeispravnaKonfiguracija, InterruptedException {
        preklapanjePokretanja = predlozakPokretanja.matcher(String.join(" ", args));
        if (!preklapanjePokretanja.matches()) {
            throw new IllegalArgumentException("Neispravan format komande.");
        }

        otvoriKlijentskiKanal();
        obradiDatoteku(args[1]);
        zatvoriKlijentskiKanal();
    }

    /**
     * Obrađuje linije iz CSV datoteke za simulaciju i šalje komande poslužitelju.
     *
     * @param putanjaDatoteke putanja do CSV datoteke s podacima za simulaciju
     * @throws IOException ako dođe do problema pri čitanju datoteke
     */
    private void obradiDatoteku(String putanjaDatoteke) throws IOException {
        try (BufferedReader citac = new BufferedReader(new FileReader(putanjaDatoteke))) {
            citac.readLine();

            String linija;
            boolean prviRedak = true;
            while ((linija = citac.readLine()) != null) {
                if (prviRedak) {
                    prethodnoVrijeme = Long.parseLong(linija.split(",")[0]);
                    prviRedak = false;
                }
                obradiLiniju(linija);
                Thread.sleep(trajanjePauze);
            }
        } catch (NumberFormatException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Preuzima potrebne postavke iz konfiguracijske datoteke i postavlja lokalne
     * varijable.
     *
     * @param datotekaKonfiguracije putanja do datoteke konfiguracije
     * @throws NeispravnaKonfiguracija ako datoteka ne postoji ili sadrži neispravne
     *                                 vrijednosti
     * @throws NumberFormatException   ako konfiguracijski podaci nisu u ispravnom
     *                                 formatu
     */
    private void preuzmiPostavke(String datotekaKonfiguracije)
        throws NeispravnaKonfiguracija, NumberFormatException {
        Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfiguracije);
        adresaVozila = konfig.dajPostavku("adresaVozila");
        mreznaVrataVozila = Integer.parseInt(konfig.dajPostavku("mreznaVrataVozila"));
        trajanjeSek = Integer.parseInt(konfig.dajPostavku("trajanjeSek"));
        trajanjePauze = Integer.parseInt(konfig.dajPostavku("trajanjePauze"));
    }

    /**
     * Otvora asinkroni mrežni kanal za komunikaciju s poslužiteljem.
     *
     * @throws IOException          ako dođe do problema pri otvaranju kanala
     * @throws ExecutionException   ako povezivanje ne uspije
     * @throws InterruptedException ako dretva bude prekinuta tijekom povezivanja
     */
    private void otvoriKlijentskiKanal()
        throws IOException, ExecutionException, InterruptedException {
        klijentskiKanal = AsynchronousSocketChannel.open();
        klijentskiKanal.connect(new InetSocketAddress(adresaVozila, mreznaVrataVozila)).get();
    }

    /**
     * Obrađuje pojedinačnu liniju iz CSV datoteke simulacije. Razdvaja liniju na
     * dijelove, šalje komandu poslužitelju pomoću tih dijelova, ažurira vrijeme iz
     * prethodne linije i inkrementira redni broj komande.
     *
     * @param linija tekstualni redak iz CSV datoteke koji sadrži podatke potrebne
     *               za simulaciju
     * @throws InterruptedException ako dretva bude prekinuta tijekom pauze koja se
     *                              odvija pri slanju komande
     */
    private void obradiLiniju(String linija) throws InterruptedException {
        String[] dijelovi = linija.split(",");
        posaljiKomanduVozilu(dijelovi);
        prethodnoVrijeme = Long.parseLong(dijelovi[0]);
        redniBroj++;
    }

    /**
     * Šalje formatiranu komandu poslužitelju koristeći asinkroni mrežni kanal.
     *
     * @param dijelovi podijeljeni dijelovi linije iz CSV datoteke
     * @throws InterruptedException     ako dretva bude prekinuta tijekom slanja
     * @throws IllegalArgumentException ako su podaci iz datoteke neispravni
     */
    private void posaljiKomanduVozilu(String[] dijelovi)
        throws InterruptedException, IllegalArgumentException {
        long trenutnoVrijeme = Long.parseLong(dijelovi[0]);
        long razlikaVremena = trenutnoVrijeme - prethodnoVrijeme;
        if (razlikaVremena < 0 || dijelovi.length != 15) {
            throw new IllegalArgumentException("Neispravan format simulacije.");
        }

        double korekcija = trajanjeSek / 1000.0;
        long pauza = (long) (razlikaVremena * korekcija);
        int idVozila = Integer.parseInt(preklapanjePokretanja.group("id"));

        Thread.sleep(pauza);

        StringBuilder komanda = new StringBuilder();
        komanda.append("VOZILO ").append(idVozila).append(" ").append(redniBroj).append(" ")
            .append(String.join(" ", dijelovi)).append("\n");

        klijentskiKanal.write(ByteBuffer.wrap(komanda.toString().getBytes()));
    }

    /**
     * Zatvara asinkroni mrežni kanal nakon završetka simulacije.
     *
     * @throws IOException ako dođe do problema pri zatvaranju kanala
     */
    private void zatvoriKlijentskiKanal() throws IOException {
        klijentskiKanal.shutdownInput();
        klijentskiKanal.shutdownOutput();
        klijentskiKanal.close();
    }
}
