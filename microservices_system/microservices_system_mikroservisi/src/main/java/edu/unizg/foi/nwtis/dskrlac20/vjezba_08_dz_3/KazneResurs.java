/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Kazne;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.pomocnici.KazneFacade;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.pomocnici.VozilaFacade;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Kazna;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * REST Web Service uz korištenje klase Kazna
 *
 * @author Dragutin Kermek
 */
@Path("nwtis/v1/api/kazne")
@RequestScoped
public class KazneResurs {
  @Inject
  KazneFacade kazneFacade;
  @Inject
  VozilaFacade vozilaFacade;
  private String putanjaNadzora;
  private String adresaKazne;
  private Integer mreznaVrataKazne;

  /**
   * Inicijalizira resurs za rad s kaznama.
   */
  @PostConstruct
  private void pripremiResurse() {
    System.out.println("Pokrećem REST: " + this.getClass().getName());
    try {
      preuzmiPostavke();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Dohvaća sve kazne ili kazne u zadanom vremenskom rasponu ako su zadani parametri.
   *
   * @param odVremena početno vrijeme raspona
   * @param doVremena završno vrijeme raspona
   * @return odgovor s listom kazni
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response dohvatiSveKazne(@QueryParam("od") Long odVremena,
      @QueryParam("do") Long doVremena) {
    var odgovor = (odVremena != null && doVremena != null) ?
        pretvoriKolekcijuKazna(kazneFacade.dohvatiKazneURasponu(odVremena, doVremena)) :
        pretvoriKolekcijuKazna(kazneFacade.dohvatiSveKazne());
    return Response.status(Response.Status.OK).entity(odgovor).build();
  }

  /**
   * Dohvaća kaznu prema rednom broju.
   *
   * @param rb redni broj kazne
   * @return odgovor s kaznom ili porukom o grešci
   */
  @Path("{rb}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response dohvatiKaznu(@PathParam("rb") int rb) {
    var kazna = pretvoriKazna(kazneFacade.dohvatiKaznu(rb));
    var odgovor = (kazna != null) ? kazna : "Navedena kazna ne postoji.";
    return Response.status(kazna != null ? Response.Status.OK : Response.Status.NOT_FOUND)
        .entity(odgovor).build();
  }

  /**
   * Dohvaća kazne za određeno vozilo prema id-u, opcionalno u zadanom vremenskom rasponu.
   *
   * @param id        ID vozila
   * @param odVremena početno vrijeme raspona
   * @param doVremena završno vrijeme raspona
   * @return odgovor s listom kazni
   */
  @Path("/vozilo/{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response dohvatiKazneZaVozilo(@PathParam("id") int id, @QueryParam("od") Long odVremena,
      @QueryParam("do") Long doVremena) {
    var voziloEntitet = vozilaFacade.find(id);

    if (voziloEntitet == null) {
      return Response.status(Response.Status.OK).entity("Vozilo ne postoji.").build();
    }

    var odgovor = (odVremena != null && doVremena != null) ?
        pretvoriKolekcijuKazna(kazneFacade.dohvatiKazneVozilaURasponu(id, odVremena, doVremena)) :
        pretvoriKolekcijuKazna(kazneFacade.dohvatiKazneVozila(id));
    return Response.status(Response.Status.OK).entity(odgovor).build();
  }

  /**
   * Provjerava stanje poslužitelja kazni.
   *
   * @return odgovor s informacijom o stanju poslužitelja
   */
  @HEAD
  @Produces({MediaType.APPLICATION_JSON})
  public Response provjeriStanjePosluziteljaKazni() {
    var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(adresaKazne, mreznaVrataKazne, "TEST");

    var odgovorEntitet =
        (odgovor != null) ? "PosluziteljKazni je aktivan." : "PosluziteljKazni nije aktivan.";
    return Response.status(
            odgovor != null ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR)
        .entity(odgovorEntitet).build();
  }

  /**
   * Dodaje novu kaznu.
   *
   * @param novaKazna objekt nove kazne
   * @return odgovor s informacijom o rezultatu dodavanja kazne
   */
  @POST
  @Produces({MediaType.APPLICATION_JSON})
  public Response dodajKaznu(Kazna novaKazna) {
    var kazna = pretvoriKazna(novaKazna);
    if (kazna == null) {
      return Response.status(Response.Status.OK).entity("Vozilo ne postoji.").build();
    }

    var odgovor = kazneFacade.dodajKaznu(kazna);
    var odgovorEntitet =
        (odgovor) ? "Kazna uspjesno dodana." : "Neuspjesni upis kazne u bazu podataka.";

    if (odgovor) {
      Client client = ClientBuilder.newClient();

      try (Response nadzoriResponse = client.target(putanjaNadzora).path("/nwtis/v1/api/nadzori/kazne")
          .request(MediaType.APPLICATION_JSON)
          .post(Entity.entity(novaKazna, MediaType.APPLICATION_JSON))) {

        if (nadzoriResponse.getStatus() != Response.Status.OK.getStatusCode()) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity("Kazna uspjesno dodana, ali slanje na NadzoriResurs nije uspjelo.")
              .build();
        }
      } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Greška prilikom slanja na NadzoriResurs.")
            .build();
      }
    }

    return Response.status(odgovor ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR)
        .entity(odgovorEntitet).build();
  }

  /**
   * Pretvara objekt tipa Kazna u objekt tipa Kazne.
   *
   * @param kazna objekt tipa Kazna koji se pretvara
   * @return objekt tipa Kazne ili null ako povezano vozilo nije pronađeno
   */
  private Kazne pretvoriKazna(Kazna kazna) {
    var kazne = new Kazne();
    kazne.setBrzina(kazna.getBrzina());
    kazne.setGpsduzina(kazna.getGpsDuzina());
    kazne.setGpssirina(kazna.getGpsSirina());
    kazne.setGpsduzinaradar(kazna.getGpsDuzinaRadara());
    kazne.setGpssirinaradar(kazna.getGpsSirinaRadara());
    kazne.setVrijemepocetak(kazna.getVrijemePocetak());
    kazne.setVrijemekraj(kazna.getVrijemeKraj());

    var vozilo = vozilaFacade.find(kazna.getId());

    if (vozilo == null)
      return null;

    kazne.setId(vozilo);
    return kazne;
  }

  /**
   * Pretvara objekt tipa Kazne u objekt tipa Kazna.
   *
   * @param kazne objekt tipa Kazne koji se pretvara
   * @return objekt tipa Kazna ili null ako je kazne null
   */
  private Kazna pretvoriKazna(Kazne kazne) {
    if (kazne == null) {
      return null;
    }
    return new Kazna(kazne.getRb(), kazne.getId().getVozilo(), kazne.getVrijemepocetak(), kazne.getVrijemekraj(),
        kazne.getBrzina(), kazne.getGpssirina(), kazne.getGpsduzina(), kazne.getGpssirinaradar(),
        kazne.getGpsduzinaradar());
  }

  /**
   * Pretvara kolekciju objekata tipa Kazne u kolekciju objekata tipa Kazna.
   *
   * @param kazne popis objekata tipa Kazne koji se pretvaraju
   * @return kolekcija objekata tipa Kazna
   */
  private List<Kazna> pretvoriKolekcijuKazna(List<Kazne> kazne) {
    var kaznaKolekcija = new ArrayList<Kazna>();
    for (Kazne k : kazne) {
      var kazna = pretvoriKazna(k);
      kaznaKolekcija.add(kazna);
    }

    System.out.println("Vracam: " + kaznaKolekcija.getFirst());
    return kaznaKolekcija;
  }

  /**
   * Preuzima postavke iz konfiguracijske datoteke.
   *
   * @throws RuntimeException ako dođe do greške prilikom preuzimanja postavki
   */
  private void preuzmiPostavke() throws RuntimeException {
    var config = Main.getKonfiguracija();
    adresaKazne = config.get("app.kazne.adresa").asString().orElse("localhost");
    mreznaVrataKazne = config.get("app.kazne.mreznaVrata").asInt().orElse(8020);
    putanjaNadzora = config.get("webservis.klijenti.nadzor.baseuri").asString().orElse("localhost");
  }
}
