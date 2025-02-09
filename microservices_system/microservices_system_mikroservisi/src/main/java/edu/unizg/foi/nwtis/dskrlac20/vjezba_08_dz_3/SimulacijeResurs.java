package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Voznje;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.pomocnici.VoznjeFacade;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Vozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Path("nwtis/v1/api/simulacije")
public class SimulacijeResurs {
  @Inject
  VoznjeFacade voznjeFacade;
  private Integer mreznaVrataPosluziteljaZaVozila;
  private String adresaPosluziteljaZaVozila;

  /**
   * Inicijalizira resurs za rad s vožnjama vozila.
   */
  @PostConstruct
  private void pripremiVozila() {
    System.out.println("Pokrećem REST: " + this.getClass().getName());
    try {
      preuzmiPostavke();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Dohvaća sve vožnje ili vožnje u zadanom vremenskom rasponu ako su zadani parametri.
   *
   * @param odVremena početno vrijeme raspona
   * @param doVremena završno vrijeme raspona
   * @return odgovor s listom vožnji
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response dohvatiSveVoznje(@QueryParam("od") Long odVremena,
      @QueryParam("do") Long doVremena) {
    return Response.status(Response.Status.OK).entity(odVremena != null && doVremena != null ?
        pretvoriKolekcijuPracenevoznje(voznjeFacade.dohvatiVoznjeURasponu(odVremena, doVremena)) :
        Collections.emptyList()).build();
  }

  /**
   * Dohvaća sve vožnje određenog vozila prema id-u, opcionalno u zadanom vremenskom rasponu.
   *
   * @param id        ID vozila
   * @param odVremena početno vrijeme raspona
   * @param doVremena završno vrijeme raspona
   * @return odgovor s listom vožnji
   */
  @Path("/vozilo/{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response dohvatiSveVoznjeVozila(@PathParam("id") int id, @QueryParam("od") Long odVremena,
      @QueryParam("do") Long doVremena) {
    return Response.status(Response.Status.OK).entity(odVremena != null && doVremena != null ?
        pretvoriKolekcijuPracenevoznje(
            voznjeFacade.dohvatiVoznjeVozilaURasponu(id, odVremena, doVremena)) :
        pretvoriKolekcijuPracenevoznje(voznjeFacade.dohvatiVoznjeVozila(id))).build();
  }

  /**
   * Dodaje novu praćenu vožnju.
   *
   * @param novoVozilo objekt nove vožnje
   * @return odgovor s informacijom o rezultatu dodavanja vožnje
   */
  @POST
  @Produces({MediaType.APPLICATION_JSON})
  public Response dodajVoznju(Vozila novoVozilo) {
    var odgovor = voznjeFacade.dodajVoznju(pretvoriVozila(novoVozilo));

    String poruka =
        (odgovor) ? "Voznja uspjesno dodana." : "Neuspješni upis voznje u bazu podataka.";

    if (!odgovor) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(poruka).build();
    }

    MrezneOperacije.posaljiZahtjevPosluzitelju(adresaPosluziteljaZaVozila,
        mreznaVrataPosluziteljaZaVozila, kreirajKomanduVozila(novoVozilo));

    return Response.status(Response.Status.OK).entity(poruka).build();
  }

  /**
   * Kreira komandu vozila za slanje poslužitelju.
   *
   * @param vozilo objekt vozila
   * @return string s komandom vozila
   */
  private String kreirajKomanduVozila(Vozila vozilo) {
    StringBuilder sb = new StringBuilder();
    sb.append("VOZILO ").append(vozilo.getId()).append(" ").append(vozilo.getBroj()).append(" ")
        .append(vozilo.getVrijeme()).append(" ").append(vozilo.getBrzina()).append(" ")
        .append(vozilo.getSnaga()).append(" ").append(vozilo.getStruja()).append(" ")
        .append(vozilo.getVisina()).append(" ").append(vozilo.getGpsBrzina()).append(" ")
        .append(vozilo.getTempVozila()).append(" ").append(vozilo.getPostotakBaterija()).append(" ")
        .append(vozilo.getNaponBaterija()).append(" ").append(vozilo.getKapacitetBaterija())
        .append(" ").append(vozilo.getTempBaterija()).append(" ").append(vozilo.getPreostaloKm())
        .append(" ").append(vozilo.getUkupnoKm()).append(" ").append(vozilo.getGpsSirina())
        .append(" ").append(vozilo.getGpsDuzina());
    return sb.toString();
  }

  /**
   * Preuzima postavke iz konfiguracijske datoteke.
   *
   * @throws RuntimeException ako dođe do greške prilikom preuzimanja postavki
   */
  private void preuzmiPostavke() throws RuntimeException {
    var config = Main.getKonfiguracija();
    adresaPosluziteljaZaVozila = config.get("app.vozila.adresa").asString().orElse("localhost");
    mreznaVrataPosluziteljaZaVozila = config.get("app.vozila.mreznaVrata").asInt().orElse(8001);
  }

  /**
   * Pretvara objekt tipa Vozila u objekt tipa Voznje.
   *
   * @param vozilo objekt tipa Vozila koji se pretvara
   * @return objekt tipa Voznje ili null ako je vozilo null
   */
  private Voznje pretvoriVozila(Vozila vozilo) {
    if (vozilo == null) {
      return null;
    }

    Voznje voznje = new Voznje();
    voznje.setBroj(vozilo.getBroj());
    voznje.setVrijeme(vozilo.getVrijeme());
    voznje.setBrzina(vozilo.getBrzina());
    voznje.setSnaga(vozilo.getSnaga());
    voznje.setStruja(vozilo.getStruja());
    voznje.setVisina(vozilo.getVisina());
    voznje.setGpsbrzina(vozilo.getGpsBrzina());
    voznje.setTempvozila(vozilo.getTempVozila());
    voznje.setPostotakbaterija(vozilo.getPostotakBaterija());
    voznje.setNaponbaterija(vozilo.getNaponBaterija());
    voznje.setKapacitetbaterija(vozilo.getKapacitetBaterija());
    voznje.setTempbaterija(vozilo.getTempBaterija());
    voznje.setPreostalokm(vozilo.getPreostaloKm());
    voznje.setUkupnokm(vozilo.getUkupnoKm());
    voznje.setGpssirina(vozilo.getGpsSirina());
    voznje.setGpsduzina(vozilo.getGpsDuzina());
    voznje.setId(vozilo.getId());

    return voznje;
  }

  /**
   * Pretvara objekt tipa Voznje u objekt tipa Vozila.
   *
   * @param voznje objekt tipa Voznje koji se pretvara
   * @return objekt tipa Vozila ili null ako je voznje null
   */
  private Vozila pretvoriVoznje(Voznje voznje) {
    if (voznje == null) {
      return null;
    }

    return new Vozila(voznje.getId(), voznje.getBroj(), voznje.getVrijeme(), voznje.getBrzina(),
        voznje.getSnaga(), voznje.getStruja(), voznje.getVisina(), voznje.getGpsbrzina(),
        voznje.getTempvozila(), voznje.getPostotakbaterija(), voznje.getNaponbaterija(),
        voznje.getKapacitetbaterija(), voznje.getTempbaterija(), voznje.getPreostalokm(),
        voznje.getUkupnokm(), voznje.getGpssirina(), voznje.getGpsduzina());
  }

  /**
   * Pretvara kolekciju objekata tipa Voznje u kolekciju objekata tipa Vozila.
   *
   * @param voznje popis objekata tipa Voznje koji se pretvaraju
   * @return kolekcija objekata tipa Vozila
   */
  private List<Vozila> pretvoriKolekcijuPracenevoznje(List<Voznje> voznje) {
    var kaznaKolekcija = new ArrayList<Vozila>();
    for (Voznje k : voznje) {
      var kazna = pretvoriVoznje(k);
      kaznaKolekcija.add(kazna);
    }
    return kaznaKolekcija;
  }
}
