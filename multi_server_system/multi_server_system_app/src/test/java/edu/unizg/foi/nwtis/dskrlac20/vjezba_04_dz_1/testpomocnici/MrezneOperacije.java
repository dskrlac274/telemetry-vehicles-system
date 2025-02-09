package edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.testpomocnici;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Klasa {@code MrezneOperacije} pruža statičku metodu za provjeru dostupnosti
 * mrežnog porta na zadanoj adresi te se korsit u testovima
 *
 * @author dskrlac20
 */
public class MrezneOperacije {

	/**
	 * Provjerava aktivnost mrežnog porta na zadanoj IP adresi. Ova metoda pokušava
	 * uspostaviti vezu na zadanu IP adresu i port..
	 *
	 * @param adresa IP adresa ili domenski naziv servera čiju dostupnost porta
	 *               treba provjeriti.
	 * @param port   Broj porta koji se provjerava.
	 * @return {@code true} ako je port dostupan, {@code false} u suprotnom.
	 */
	public static boolean provjeriAktivnostPorta(String adresa, int port) {
		InetSocketAddress isa = new InetSocketAddress(adresa, port);
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		try (Socket s = new Socket()) {
			s.connect(isa, 70);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
