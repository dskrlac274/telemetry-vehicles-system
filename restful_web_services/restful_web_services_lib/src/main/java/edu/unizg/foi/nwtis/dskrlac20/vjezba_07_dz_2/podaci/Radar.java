package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci;

/**
 * Klasa {@code Radar} predstavlja radar s informacijama kao što su id, adresa
 * radara, mrežna vrata, GPS koordinati i maksimalna udaljenost radara.
 *
 * @author dskrlac20
 */
public class Radar {
	private int id;
	private String adresaRadara;
	private int mreznaVrataRadara;
	private double gpsSirina;
	private double gpsDuzina;
	private int maksUdaljenost;

	/**
	 * Prazan konstruktor za kreiranje instance klase {@code Radar}.
	 */
	public Radar() {
	}

	/**
	 * Konstruktor sa svim atributima radara.
	 *
	 * @param id                ID radara
	 * @param adresaRadara      adresa radara
	 * @param mreznaVrataRadara mrežna vrata radara
	 * @param gpsSirina         GPS širina radara
	 * @param gpsDuzina         GPS dužina radara
	 * @param maksUdaljenost    maksimalna udaljenost radara
	 */
	public Radar(int id, String adresaRadara, int mreznaVrataRadara, double gpsSirina,
			double gpsDuzina, int maksUdaljenost) {
		this.id = id;
		this.adresaRadara = adresaRadara;
		this.mreznaVrataRadara = mreznaVrataRadara;
		this.gpsSirina = gpsSirina;
		this.gpsDuzina = gpsDuzina;
		this.maksUdaljenost = maksUdaljenost;
	}

	/**
	 * Vraća id radara.
	 *
	 * @return id radara
	 */
	public int getId() {
		return id;
	}

	/**
	 * Postavlja id radara.
	 *
	 * @param id id radara
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Vraća adresu radara.
	 *
	 * @return adresa radara
	 */
	public String getAdresaRadara() {
		return adresaRadara;
	}

	/**
	 * Postavlja adresu radara.
	 *
	 * @param adresaRadara adresa radara
	 */
	public void setAdresaRadara(String adresaRadara) {
		this.adresaRadara = adresaRadara;
	}

	/**
	 * Vraća mrežna vrata radara.
	 *
	 * @return mrežna vrata radara
	 */
	public int getMreznaVrataRadara() {
		return mreznaVrataRadara;
	}

	/**
	 * Postavlja mrežna vrata radara.
	 *
	 * @param mreznaVrataRadara mrežna vrata radara
	 */
	public void setMreznaVrataRadara(int mreznaVrataRadara) {
		this.mreznaVrataRadara = mreznaVrataRadara;
	}

	/**
	 * Vraća GPS širinu radara.
	 *
	 * @return GPS širina radara
	 */
	public double getGpsSirina() {
		return gpsSirina;
	}

	/**
	 * Postavlja GPS širinu radara.
	 *
	 * @param gpsSirina GPS širina radara
	 */
	public void setGpsSirina(double gpsSirina) {
		this.gpsSirina = gpsSirina;
	}

	/**
	 * Vraća GPS dužinu radara.
	 *
	 * @return GPS dužina radara
	 */
	public double getGpsDuzina() {
		return gpsDuzina;
	}

	/**
	 * Postavlja GPS dužinu radara.
	 *
	 * @param gpsDuzina GPS dužina radara
	 */
	public void setGpsDuzina(double gpsDuzina) {
		this.gpsDuzina = gpsDuzina;
	}

	/**
	 * Vraća maksimalnu udaljenost radara.
	 *
	 * @return maksimalna udaljenost radara
	 */
	public int getMaksUdaljenost() {
		return maksUdaljenost;
	}

	/**
	 * Postavlja maksimalnu udaljenost radara.
	 *
	 * @param maksUdaljenost maksimalna udaljenost radara
	 */
	public void setMaksUdaljenost(int maksUdaljenost) {
		this.maksUdaljenost = maksUdaljenost;
	}
}
