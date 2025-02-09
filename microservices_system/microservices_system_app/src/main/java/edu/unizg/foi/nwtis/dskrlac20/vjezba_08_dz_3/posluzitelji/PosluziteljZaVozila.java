package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.posluzitelji;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Queue;
import java.util.concurrent.*;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.posluzitelji.radnici.RadnikZaVozila;

/**
 * Klasa {@code PosluziteljZaVozila} implementira sučelje {@code Runnable} i
 * služi kao asinkroni mrežni poslužitelj za upravljanje vezama s vozilima. Ova
 * klasa koristi asinkroni server socket kanal za prihvat veza od klijenata,
 * ograničavajući broj aktivnih veza na maksimalni broj vozila definiran u
 * centralnom sustavu. Svaka veza se obrađuje u zasebnoj virtualnoj dretvi kroz
 * instancu {@code RadnikZaVozila}.
 *
 * @author dskrlac20
 */
public class PosluziteljZaVozila implements Runnable {
    private String baseUri;
    public final CentralniSustav centralniSustav;
    private final ThreadFactory tvornicaVirtualnihDretvi = Thread.ofVirtual().factory();
    public static Queue<AsynchronousSocketChannel> aktivniKanali = new ConcurrentLinkedDeque<>();

    /**
     * Konstruktor klase {@code PosluziteljZaVozila} koji inicijalizira centralni
     * sustav. Prima referencu na centralni sustav koji omogućava pristup
     * zajedničkim resursima i postavkama.
     *
     * @param centralniSustav Instanca centralnog sustava koja upravlja
     *                        registracijom i podacima vozila.
     */
    public PosluziteljZaVozila(CentralniSustav centralniSustav, String baseUri) {
        this.centralniSustav = centralniSustav;
        this.baseUri = baseUri;
    }

    /**
     * Glavna izvršna metoda za dretvu. Upravlja asinkronim server uticnicom koji
     * sluša za nove klijentske veze. Veze se prihvaćaju sve dok dretva nije
     * prekinuta, a broj aktivnih veza se održava unutar maksimalno dozvoljenog
     * broja definiranog u centralnom sustavu.
     */
    @Override
    public void run() {
        try (AsynchronousServerSocketChannel serverskiKanal = AsynchronousServerSocketChannel
            .open()) {
            serverskiKanal.bind(new InetSocketAddress(centralniSustav.mreznaVrataVozila));
            while (!Thread.currentThread().isInterrupted()) {
                aktivniKanali.removeIf(kanal -> !kanal.isOpen());

                AsynchronousSocketChannel klijentskiKanal = serverskiKanal.accept().get();
                if (aktivniKanali.size() < centralniSustav.maksVozila) {

                    aktivniKanali.add(klijentskiKanal);

                    tvornicaVirtualnihDretvi.newThread(new RadnikZaVozila(this,
                            klijentskiKanal, baseUri))
                        .start();
                } else {
                    klijentskiKanal.close();
                }
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
    }
}
