package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci;

import jakarta.mvc.binding.MvcBinding;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;

/**
 * Klasa {@code VoziloZrno} predstavlja podatke vozila za simulaciju. Podaci
 * uključuju identifikacijske podatke, karakteristike vozila, GPS koordinate i
 * status baterije.
 *
 * @author dskrlac20
 */
public class VoziloZrno {
  @MvcBinding
  @FormParam("id")
  @NotNull
  private int id;

  @MvcBinding
  @FormParam("broj")
  @NotNull
  private int broj;

  @MvcBinding
  @FormParam("vrijeme")
  @NotNull
  private long vrijeme;

  @MvcBinding
  @FormParam("brzina")
  @NotNull
  private double brzina;

  @MvcBinding
  @FormParam("snaga")
  @NotNull
  private double snaga;

  @MvcBinding
  @FormParam("struja")
  @NotNull
  private double struja;

  @MvcBinding
  @FormParam("visina")
  @NotNull
  private double visina;

  @MvcBinding
  @FormParam("gpsBrzina")
  @NotNull
  private double gpsBrzina;

  @MvcBinding
  @FormParam("tempVozila")
  @NotNull
  private int tempVozila;

  @MvcBinding
  @FormParam("postotakBaterija")
  @NotNull
  private int postotakBaterija;

  @MvcBinding
  @FormParam("naponBaterija")
  @NotNull
  private double naponBaterija;

  @MvcBinding
  @FormParam("kapacitetBaterija")
  @NotNull
  private int kapacitetBaterija;

  @MvcBinding
  @FormParam("tempBaterija")
  @NotNull
  private int tempBaterija;

  @MvcBinding
  @FormParam("preostaloKm")
  @NotNull
  private double preostaloKm;

  @MvcBinding
  @FormParam("ukupnoKm")
  @NotNull
  private double ukupnoKm;

  @MvcBinding
  @FormParam("gpsSirina")
  @NotNull
  private double gpsSirina;

  @MvcBinding
  @FormParam("gpsDuzina")
  @NotNull
  private double gpsDuzina;

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
   * Vraća vrijeme vožnje u sekundama.
   *
   * @return vrijeme vožnje u sekundama
   */
  public long getVrijeme() {
    return vrijeme;
  }

  /**
   * Postavlja vrijeme vožnje u sekundama.
   *
   * @param vrijeme vrijeme vožnje u sekundama
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
   * Vraća postotak baterije vozila.
   *
   * @return postotak baterije vozila
   */
  public int getPostotakBaterija() {
    return postotakBaterija;
  }

  /**
   * Postavlja postotak baterije vozila.
   *
   * @param postotakBaterija postotak baterije vozila
   */
  public void setPostotakBaterija(int postotakBaterija) {
    this.postotakBaterija = postotakBaterija;
  }

  /**
   * Vraća napon baterije vozila.
   *
   * @return napon baterije vozila
   */
  public double getNaponBaterija() {
    return naponBaterija;
  }

  /**
   * Postavlja napon baterije vozila.
   *
   * @param naponBaterija napon baterije vozila
   */
  public void setNaponBaterija(double naponBaterija) {
    this.naponBaterija = naponBaterija;
  }

  /**
   * Vraća kapacitet baterije vozila.
   *
   * @return kapacitet baterije vozila
   */
  public int getKapacitetBaterija() {
    return kapacitetBaterija;
  }

  /**
   * Postavlja kapacitet baterije vozila.
   *
   * @param kapacitetBaterija kapacitet baterije vozila
   */
  public void setKapacitetBaterija(int kapacitetBaterija) {
    this.kapacitetBaterija = kapacitetBaterija;
  }

  /**
   * Vraća temperaturu baterije vozila.
   *
   * @return temperatura baterije vozila
   */
  public int getTempBaterija() {
    return tempBaterija;
  }

  /**
   * Postavlja temperaturu baterije vozila.
   *
   * @param tempBaterija temperatura baterije vozila
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
