package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.rest;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jms.PrijenosnikKazni;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Kazna;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Poruka;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Date;

/**
 * REST resurs za upravljanje kaznama vozila.
 * Omogućuje dodavanje nove kazne vozilu preko HTTP POST zahtjeva.
 */
@Path("nwtis/v1/api/nadzori/kazne")
@RequestScoped
public class NadzoriResurs {
  @Inject
  PrijenosnikKazni prijenosnikKazni;

  /**
   * Metoda za primanje HTTP POST zahtjeva za dodavanje nove kazne vozilu.
   * Stvara poruku o novoj kazni, šalje je na obradu preko {@link PrijenosnikKazni} komponente,
   * i vraća odgovor o uspješnosti operacije.
   *
   * @param novaKazna Podaci o novoj kazni vozila dobiveni iz HTTP zahtjeva
   * @return Odgovor o uspješnosti operacije kao HTTP odgovor s statusom OK ili greškom
   */
  @POST
  @Produces({MediaType.APPLICATION_JSON})
  public Response postJsonDodajKaznu(Kazna novaKazna) {
    Poruka poruka = new Poruka();
    poruka.setNaslov("Nova kazna za vozilo: " + novaKazna.getId());
    poruka.setSadrzaj("Kreirana kazna za vozilo: " + novaKazna.getId() +
        " zbog brzine od: " + novaKazna.getBrzina() + " km/h");
    poruka.setVrijeme(new Date());
    poruka.setKazna(novaKazna);

    boolean odgovor = prijenosnikKazni.novaKazna(poruka);
    if (odgovor) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Neuspješna obrada kazne.").build();
    }
  }
}
