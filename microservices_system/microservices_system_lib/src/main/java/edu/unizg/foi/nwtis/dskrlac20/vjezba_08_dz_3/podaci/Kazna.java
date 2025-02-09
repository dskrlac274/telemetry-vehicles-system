package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci;

import java.io.Serial;
import java.io.Serializable;

/**
 * Klasa {@code Kazna} predstavlja kaznu vozila s informacijama kao što su redni
 * broj, id vozila, vrijeme početka i kraja, brzina, GPS koordinati vozila i
 * radara.
 *
 * @author dskrlac20
 */
public class Kazna implements Serializable {
	@Serial
	private static final long serialVersionUID = 6265748844499929090L;
	private int rb;
	private int id;
	private long vrijemePocetak;
	private long vrijemeKraj;
	private double brzina;
	private double gpsSirina;
	private double gpsDuzina;
	private double gpsSirinaRadara;
	private double gpsDuzinaRadara;

	/**
	 * Prazan konstruktor za kreiranje instance klase {@code Kazna}.
	 */
	public Kazna() {
	}

	/**
	 * Konstruktor sa svim atributima kazne.
	 *
	 * @param rb              redni broj kazne
	 * @param id              ID vozila
	 * @param vrijemePocetak  vrijeme početka kazne
	 * @param vrijemeKraj     vrijeme kraja kazne
	 * @param brzina          brzina vozila
	 * @param gpsSirina       GPS širina vozila
	 * @param gpsDuzina       GPS dužina vozila
	 * @param gpsSirinaRadara GPS širina radara
	 * @param gpsDuzinaRadara GPS dužina radara
	 */
	public Kazna(int rb, int id, long vrijemePocetak, long vrijemeKraj, double brzina,
			double gpsSirina, double gpsDuzina, double gpsSirinaRadara, double gpsDuzinaRadara) {
		this.rb = rb;
		this.id = id;
		this.vrijemePocetak = vrijemePocetak;
		this.vrijemeKraj = vrijemeKraj;
		this.brzina = brzina;
		this.gpsSirina = gpsSirina;
		this.gpsDuzina = gpsDuzina;
		this.gpsSirinaRadara = gpsSirinaRadara;
		this.gpsDuzinaRadara = gpsDuzinaRadara;
	}

	/**
	 * Konstruktor sa svim atributima kazne osim rednog broja.
	 *
	 * @param id              ID vozila
	 * @param vrijemePocetak  vrijeme početka kazne
	 * @param vrijemeKraj     vrijeme kraja kazne
	 * @param brzina          brzina vozila
	 * @param gpsSirina       GPS širina vozila
	 * @param gpsDuzina       GPS dužina vozila
	 * @param gpsSirinaRadara GPS širina radara
	 * @param gpsDuzinaRadara GPS dužina radara
	 */
	public Kazna(int id, long vrijemePocetak, long vrijemeKraj, double brzina, double gpsSirina,
			double gpsDuzina, double gpsSirinaRadara, double gpsDuzinaRadara) {
		this.id = id;
		this.vrijemePocetak = vrijemePocetak;
		this.vrijemeKraj = vrijemeKraj;
		this.brzina = brzina;
		this.gpsSirina = gpsSirina;
		this.gpsDuzina = gpsDuzina;
		this.gpsSirinaRadara = gpsSirinaRadara;
		this.gpsDuzinaRadara = gpsDuzinaRadara;
	}

	/**
	 * Vraća redni broj kazne.
	 *
	 * @return redni broj kazne
	 */
	public int getRb() {
		return rb;
	}

	/**
	 * Postavlja redni broj kazne.
	 *
	 * @param rb redni broj kazne
	 */
	public void setRb(int rb) {
		this.rb = rb;
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
	 * Vraća vrijeme početka kazne.
	 *
	 * @return vrijeme početka kazne
	 */
	public long getVrijemePocetak() {
		return vrijemePocetak;
	}

	/**
	 * Postavlja vrijeme početka kazne.
	 *
	 * @param vrijemePocetak vrijeme početka kazne
	 */
	public void setVrijemePocetak(long vrijemePocetak) {
		this.vrijemePocetak = vrijemePocetak;
	}

	/**
	 * Vraća vrijeme kraja kazne.
	 *
	 * @return vrijeme kraja kazne
	 */
	public long getVrijemeKraj() {
		return vrijemeKraj;
	}

	/**
	 * Postavlja vrijeme kraja kazne.
	 *
	 * @param vrijemeKraj vrijeme kraja kazne
	 */
	public void setVrijemeKraj(long vrijemeKraj) {
		this.vrijemeKraj = vrijemeKraj;
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

	/**
	 * Vraća GPS širinu radara.
	 *
	 * @return GPS širina radara
	 */
	public double getGpsSirinaRadara() {
		return gpsSirinaRadara;
	}

	/**
	 * Postavlja GPS širinu radara.
	 *
	 * @param gpsSirinaRadara GPS širina radara
	 */
	public void setGpsSirinaRadara(double gpsSirinaRadara) {
		this.gpsSirinaRadara = gpsSirinaRadara;
	}

	/**
	 * Vraća GPS dužinu radara.
	 *
	 * @return GPS dužina radara
	 */
	public double getGpsDuzinaRadara() {
		return gpsDuzinaRadara;
	}

	/**
	 * Postavlja GPS dužinu radara.
	 *
	 * @param gpsDuzinaRadara GPS dužina radara
	 */
	public void setGpsDuzinaRadara(double gpsDuzinaRadara) {
		this.gpsDuzinaRadara = gpsDuzinaRadara;
	}
}
