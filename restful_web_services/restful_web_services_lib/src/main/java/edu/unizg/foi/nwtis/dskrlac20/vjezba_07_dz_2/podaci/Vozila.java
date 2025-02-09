package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci;

/**
 * Klasa {@code Vozila} predstavlja vozilo s informacijama kao što su id, broj,
 * vrijeme, brzina, snaga, struja, visina, GPS brzina, temperatura vozila,
 * postotak baterije, napon baterije, kapacitet baterije, temperatura baterije,
 * preostalo i ukupno kilometraža, te GPS koordinate.
 *
 * @author dskrlac20
 */
public class Vozila {
	private int id;
	private int broj;
	private long vrijeme;
	private double brzina;
	private double snaga;
	private double struja;
	private double visina;
	private double gpsBrzina;
	private int tempVozila;
	private int postotakBaterija;
	private double naponBaterija;
	private int kapacitetBaterija;
	private int tempBaterija;
	private double preostaloKm;
	private double ukupnoKm;
	private double gpsSirina;
	private double gpsDuzina;

	/**
	 * Prazan konstruktor za kreiranje instance klase {@code Vozila}.
	 */
	public Vozila() {
	}

	/**
	 * Konstruktor sa svim atributima vozila.
	 *
	 * @param id                ID vozila
	 * @param broj              broj vozila
	 * @param vrijeme           vrijeme
	 * @param brzina            brzina vozila
	 * @param snaga             snaga vozila
	 * @param struja            struja vozila
	 * @param visina            visina vozila
	 * @param gpsBrzina         GPS brzina vozila
	 * @param tempVozila        temperatura vozila
	 * @param postotakBaterija  postotak baterije
	 * @param naponBaterija     napon baterije
	 * @param kapacitetBaterija kapacitet baterije
	 * @param tempBaterija      temperatura baterije
	 * @param preostaloKm       preostala kilometraža
	 * @param ukupnoKm          ukupna kilometraža
	 * @param gpsSirina         GPS širina vozila
	 * @param gpsDuzina         GPS dužina vozila
	 */
	public Vozila(int id, int broj, long vrijeme, double brzina, double snaga, double struja,
			double visina, double gpsBrzina, int tempVozila, int postotakBaterija,
			double naponBaterija, int kapacitetBaterija, int tempBaterija, double preostaloKm,
			double ukupnoKm, double gpsSirina, double gpsDuzina) {
		this.id = id;
		this.broj = broj;
		this.vrijeme = vrijeme;
		this.brzina = brzina;
		this.snaga = snaga;
		this.struja = struja;
		this.visina = visina;
		this.gpsBrzina = gpsBrzina;
		this.tempVozila = tempVozila;
		this.postotakBaterija = postotakBaterija;
		this.naponBaterija = naponBaterija;
		this.kapacitetBaterija = kapacitetBaterija;
		this.tempBaterija = tempBaterija;
		this.preostaloKm = preostaloKm;
		this.ukupnoKm = ukupnoKm;
		this.gpsSirina = gpsSirina;
		this.gpsDuzina = gpsDuzina;
	}

	/**
	 * Vraća id vozila.
	 *
	 * @return id vozila
	 */
	public int getId() {
		return id;
	}

	/**
	 * Postavlja id vozila.
	 *
	 * @param id id vozila
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Vraća broj vozila.
	 *
	 * @return broj vozila
	 */
	public int getBroj() {
		return broj;
	}

	/**
	 * Postavlja broj vozila.
	 *
	 * @param broj broj vozila
	 */
	public void setBroj(int broj) {
		this.broj = broj;
	}

	/**
	 * Vraća vrijeme.
	 *
	 * @return vrijeme
	 */
	public long getVrijeme() {
		return vrijeme;
	}

	/**
	 * Postavlja vrijeme.
	 *
	 * @param vrijeme vrijeme
	 */
	public void setVrijeme(long vrijeme) {
		this.vrijeme = vrijeme;
	}

	/**
	 * Vraća brzinu vozila.
	 *
	 * @return brzina vozila
	 */
	public double getBrzina() {
		return brzina;
	}

	/**
	 * Postavlja brzinu vozila.
	 *
	 * @param brzina brzina vozila
	 */
	public void setBrzina(double brzina) {
		this.brzina = brzina;
	}

	/**
	 * Vraća snagu vozila.
	 *
	 * @return snaga vozila
	 */
	public double getSnaga() {
		return snaga;
	}

	/**
	 * Postavlja snagu vozila.
	 *
	 * @param snaga snaga vozila
	 */
	public void setSnaga(double snaga) {
		this.snaga = snaga;
	}

	/**
	 * Vraća struju vozila.
	 *
	 * @return struja vozila
	 */
	public double getStruja() {
		return struja;
	}

	/**
	 * Postavlja struju vozila.
	 *
	 * @param struja struja vozila
	 */
	public void setStruja(double struja) {
		this.struja = struja;
	}

	/**
	 * Vraća visinu vozila.
	 *
	 * @return visina vozila
	 */
	public double getVisina() {
		return visina;
	}

	/**
	 * Postavlja visinu vozila.
	 *
	 * @param visina visina vozila
	 */
	public void setVisina(double visina) {
		this.visina = visina;
	}

	/**
	 * Vraća GPS brzinu vozila.
	 *
	 * @return GPS brzina vozila
	 */
	public double getGpsBrzina() {
		return gpsBrzina;
	}

	/**
	 * Postavlja GPS brzinu vozila.
	 *
	 * @param gpsBrzina GPS brzina vozila
	 */
	public void setGpsBrzina(double gpsBrzina) {
		this.gpsBrzina = gpsBrzina;
	}

	/**
	 * Vraća temperaturu vozila.
	 *
	 * @return temperatura vozila
	 */
	public int getTempVozila() {
		return tempVozila;
	}

	/**
	 * Postavlja temperaturu vozila.
	 *
	 * @param tempVozila temperatura vozila
	 */
	public void setTempVozila(int tempVozila) {
		this.tempVozila = tempVozila;
	}

	/**
	 * Vraća postotak baterije.
	 *
	 * @return postotak baterije
	 */
	public int getPostotakBaterija() {
		return postotakBaterija;
	}

	/**
	 * Postavlja postotak baterije.
	 *
	 * @param postotakBaterija postotak baterije
	 */
	public void setPostotakBaterija(int postotakBaterija) {
		this.postotakBaterija = postotakBaterija;
	}

	/**
	 * Vraća napon baterije.
	 *
	 * @return napon baterije
	 */
	public double getNaponBaterija() {
		return naponBaterija;
	}

	/**
	 * Postavlja napon baterije.
	 *
	 * @param naponBaterija napon baterije
	 */
	public void setNaponBaterija(double naponBaterija) {
		this.naponBaterija = naponBaterija;
	}

	/**
	 * Vraća kapacitet baterije.
	 *
	 * @return kapacitet baterije
	 */
	public int getKapacitetBaterija() {
		return kapacitetBaterija;
	}

	/**
	 * Postavlja kapacitet baterije.
	 *
	 * @param kapacitetBaterija kapacitet baterije
	 */
	public void setKapacitetBaterija(int kapacitetBaterija) {
		this.kapacitetBaterija = kapacitetBaterija;
	}

	/**
	 * Vraća temperaturu baterije.
	 *
	 * @return temperatura baterije
	 */
	public int getTempBaterija() {
		return tempBaterija;
	}

	/**
	 * Postavlja temperaturu baterije.
	 *
	 * @param tempBaterija temperatura baterije
	 */
	public void setTempBaterija(int tempBaterija) {
		this.tempBaterija = tempBaterija;
	}

	/**
	 * Vraća preostalu kilometražu vozila.
	 *
	 * @return preostala kilometraža vozila
	 */
	public double getPreostaloKm() {
		return preostaloKm;
	}

	/**
	 * Postavlja preostalu kilometražu vozila.
	 *
	 * @param preostaloKm preostala kilometraža vozila
	 */
	public void setPreostaloKm(double preostaloKm) {
		this.preostaloKm = preostaloKm;
	}

	/**
	 * Vraća ukupnu kilometražu vozila.
	 *
	 * @return ukupna kilometraža vozila
	 */
	public double getUkupnoKm() {
		return ukupnoKm;
	}

	/**
	 * Postavlja ukupnu kilometražu vozila.
	 *
	 * @param ukupnoKm ukupna kilometraža vozila
	 */
	public void setUkupnoKm(double ukupnoKm) {
		this.ukupnoKm = ukupnoKm;
	}

	/**
	 * Vraća GPS širinu vozila.
	 *
	 * @return GPS širina vozila
	 */
	public double getGpsSirina() {
		return gpsSirina;
	}

	/**
	 * Postavlja GPS širinu vozila.
	 *
	 * @param gpsSirina GPS širina vozila
	 */
	public void setGpsSirina(double gpsSirina) {
		this.gpsSirina = gpsSirina;
	}

	/**
	 * Vraća GPS dužinu vozila.
	 *
	 * @return GPS dužina vozila
	 */
	public double getGpsDuzina() {
		return gpsDuzina;
	}

	/**
	 * Postavlja GPS dužinu vozila.
	 *
	 * @param gpsDuzina GPS dužina vozila
	 */
	public void setGpsDuzina(double gpsDuzina) {
		this.gpsDuzina = gpsDuzina;
	}
}
