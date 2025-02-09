package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.kontroler;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.model.RestKlijentRadari;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Radar;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.mvc.binding.BindingResult;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

import java.util.List;

@Controller
@Path("radari")
@RequestScoped
public class RadariKontroler {
    @Context
    private ServletContext context;

    private RestKlijentRadari restKlijentRadari;

    /**
     * Priprema resursa.
     */
    @PostConstruct
    private void pripremiResurse() {
        try {
            String radariBaseUri = (String) context.getAttribute("webservis.radari.baseuri");
            restKlijentRadari = new RestKlijentRadari(radariBaseUri);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Inject
    private Models model;

    @Inject
    private BindingResult bindingResult;


    @Context
    private HttpServletRequest zahtjev;

    /**
     * Prikazuje sve radare.
     */
    @GET
    @View("radari.jsp")
    public void ispisRadara() {
        List<Radar> radari = restKlijentRadari.dohvatiRadare();
        model.put("radari", radari);
    }

    /**
     * Resetira sve radare i postavlja rezultat u sesiju.
     *
     * @return redirekt na stranicu radara
     */
    @POST
    @Path("reset")
    public String resetirajRadare() {
        String resetStatus = restKlijentRadari.resetirajRadare();
        HttpSession session = zahtjev.getSession();
        session.setAttribute("status", resetStatus);

        return "redirect:/radari";
    }

    /**
     * Prikazuje određeni radar prema id-u.
     *
     * @param id id radara
     */
    @GET
    @Path("{id}")
    @View("radar.jsp")
    public void ispisRadara(@PathParam("id") String id) {
        Radar radar = restKlijentRadari.dohvatiRadar(id);
        String status = restKlijentRadari.provjeriRadar(id);
        HttpSession session = zahtjev.getSession();
        session.setAttribute("status", status);

        model.put("radar", radar);
    }

    /**
     * Briše određeni radar prema id-u.
     *
     * @param id id radara
     * @return redirekt na stranicu radara
     */
    @POST
    @Path("{id}/obrisi")
    public String obrisiRadar(@PathParam("id") String id) {
        String brisanjeStatus = restKlijentRadari.obrisiRadar(id);
        HttpSession session = zahtjev.getSession();
        session.setAttribute("status", brisanjeStatus);

        return "redirect:/radari";
    }

    /**
     * Briše sve radare.
     *
     * @return redirekt na stranicu radara
     */
    @POST
    @Path("obrisiSve")
    public String obrisiSveRadare() {
        String brisanjeSvihStatus = restKlijentRadari.obrisiSveRadare();
        HttpSession session = zahtjev.getSession();
        session.setAttribute("status", brisanjeSvihStatus);

        return "redirect:/radari";
    }

    /**
     * Pretražuje radar prema id-u iz forme.
     *
     * @param id id radara
     * @return redirekt na stranicu radara s danim id-om
     */
    @POST
    @Path("pretrazi-po-id")
    public String pretrazivanjeKazne(@FormParam("id") String id) {
        Radar radar = restKlijentRadari.dohvatiRadar(id);
        model.put("radar", radar);

        return "redirect:" + "/radari/" + id;
    }
}
