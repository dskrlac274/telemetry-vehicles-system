package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci;

import jakarta.mvc.binding.MvcBinding;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;

/**
 * Klasa {@code SimulacijaZrno} predstavlja podatke potrebne za simulaciju
 * vožnje. Podaci uključuju naziv datoteke, id vozila, trajanje simulacije i
 * trajanje pauze.
 *
 * @author dskrlac20
 */
public class SimulacijaZrno {
  @FormParam("nazivDatoteke")
  @MvcBinding
  @NotNull
  private String nazivDatoteke;

  @FormParam("idVozila")
  @MvcBinding
  @NotNull
  private int idVozila;
  @FormParam("trajanjeSekunde")
  @MvcBinding
  @NotNull
  private int trajanjeSekunde;
  @FormParam("trajanjePauze")
  @MvcBinding
  @NotNull
  private int trajanjePauze;

  /**
   * Vraća naziv datoteke.
   *
   * @return naziv datoteke
   */
  public String getNazivDatoteke() {
    return nazivDatoteke;
  }

  /**
   * Postavlja naziv datoteke.
   *
   * @param nazivDatoteke naziv datoteke
   */
  public void setNazivDatoteke(String nazivDatoteke) {
    this.nazivDatoteke = nazivDatoteke;
  }

  /**
   * Vraća id vozila.
   *
   * @return id vozila
   */
  public int getIdVozila() {
    return idVozila;
  }

  /**
   * Postavlja id vozila.
   *
   * @param idVozila id vozila
   */
  public void setIdVozila(int idVozila) {
    this.idVozila = idVozila;
  }

  /**
   * Vraća trajanje simulacije u sekundama.
   *
   * @return trajanje simulacije u sekundama
   */
  public int getTrajanjeSekunde() {
    return trajanjeSekunde;
  }

  /**
   * Postavlja trajanje simulacije u sekundama.
   *
   * @param trajanjeSekunde trajanje simulacije u sekundama
   */
  public void setTrajanjeSekunde(int trajanjeSekunde) {
    this.trajanjeSekunde = trajanjeSekunde;
  }

  /**
   * Vraća trajanje pauze između simulacija u sekundama.
   *
   * @return trajanje pauze između simulacija u sekundama
   */
  public int getTrajanjePauze() {
    return trajanjePauze;
  }

  /**
   * Postavlja trajanje pauze između simulacija u sekundama.
   *
   * @param trajanjePauze trajanje pauze između simulacija u sekundama
   */
  public void setTrajanjePauze(int trajanjePauze) {
    this.trajanjePauze = trajanjePauze;
  }
}
