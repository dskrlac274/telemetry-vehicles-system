package edu.unizg.foi.nwtis.dskrlac20.vjezba_04_dz_1.testpomocnici;

import java.io.File;

/**
 * Klasa {@code DatotecneOperacije} pruža statičku metodu za brisanje datoteka
 * na disku koja se koristi u testovima.
 *
 * @author dskrlac20
 */
public class DatotecneOperacije {
	/**
	 * Briše datoteku sa zadanim imenom s diska.
	 *
	 * @param imeDatoteke Ime datoteke koju treba obrisati.
	 * @return true ako datoteka ne postoji ili je uspješno obrisana, false ako
	 *         datoteka postoji ali nije obrisana.
	 */
	public static boolean obrisiDatoteku(String imeDatoteke) {
		File f = new File(imeDatoteke);
		if (!f.exists()) {
			return true;
		} else if (f.isFile()) {
			return f.delete();
		}
		return false;
	}
}
