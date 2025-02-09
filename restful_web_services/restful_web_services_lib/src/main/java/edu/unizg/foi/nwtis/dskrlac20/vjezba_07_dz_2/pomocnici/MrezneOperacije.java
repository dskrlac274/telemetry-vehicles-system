package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.pomocnici;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Klasa MrezneOperacije.
 */
public class MrezneOperacije {

	/**
	 * Šalje zahtjev poslužitelju.
	 *
	 * @param adresa      adresa poslužitelja
	 * @param mreznaVrata mrežna vrata poslužitelja
	 * @param poruka      tekst poruke koja se šalje
	 * @return odgovor. Ako nije u redu vraća se null, inače primljeni odgovor od
	 *         poslužitelja
	 */
	public static String posaljiZahtjevPosluzitelju(String adresa, int mreznaVrata, String poruka) {
		try (Socket mreznaUticnica = new Socket(adresa, mreznaVrata)) {
			BufferedReader citac = new BufferedReader(
					new InputStreamReader(mreznaUticnica.getInputStream(), StandardCharsets.UTF_8));
			OutputStream out = mreznaUticnica.getOutputStream();
			PrintWriter pisac = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8),
					true);
			pisac.println(poruka);
			pisac.flush();
			mreznaUticnica.shutdownOutput();
			var odgovor = citac.readLine();
			mreznaUticnica.shutdownInput();
			mreznaUticnica.close();
			return odgovor;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}
