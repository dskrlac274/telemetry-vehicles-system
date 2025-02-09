/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.kontroler;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.model.RestKlijentKazne;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Kazna;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;

import java.util.List;

@Controller
@Path("kazne")
@RequestScoped
public class KazneKontroler {
    @Context
    private ServletContext context;

    private RestKlijentKazne restKlijentKazne;

    /**
     * Priprema resursa.
     */
    @PostConstruct
    private void pripremiResurse() {
        try {
            String kazneBaseUri = (String) context.getAttribute("webservis.kazne.baseuri");
            restKlijentKazne = new RestKlijentKazne(kazneBaseUri);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Inject
    private Models model;

    @Context
    private HttpServletRequest zahtjev;

    /**
     * Prikazuje sve kazne, ili kazne u određenom vremenskom rasponu ako su zadani
     * odgovarajući upitni parametri.
     *
     * @param odVremena početno vrijeme raspona
     * @param doVremena završno vrijeme raspona
     */
    @GET
    @View("kazne.jsp")
    public void ispisKazni(@QueryParam("odVremena") Long odVremena,
        @QueryParam("doVremena") Long doVremena) {
        List<Kazna> kazne;

        if (odVremena != null && doVremena != null) {
            kazne = restKlijentKazne.dohvatiKazneURasponu(odVremena, doVremena);
        } else {
            kazne = restKlijentKazne.dohvatiKazne();
        }

        model.put("kazne", kazne);
        model.put("searchActionUrl", zahtjev.getContextPath() + "/mvc/kazne");
    }

    /**
     * Provjerava stanje poslužitelja kazni i postavlja rezultat u sesiju.
     *
     * @return redirekt na stranicu kazni
     */
    @POST
    @Path("/provjeri")
    public String provjeriPosluziteljaKazni() {
        String status = restKlijentKazne.provjeriStanjePosluziteljaKazni();
        HttpSession session = zahtjev.getSession();
        session.setAttribute("status", status);

        return "redirect:" + "/kazne";
    }

    /**
     * Prikazuje određenu kaznu prema rednom broju.
     *
     * @param rb redni broj kazne
     */
    @GET
    @Path("{rb}")
    @View("kazna.jsp")
    public void ispisKazne(@PathParam("rb") String rb) {
        Kazna kazna = restKlijentKazne.dohvatiKaznu(rb);
        model.put("kazna", kazna);
    }

    /**
     * Prikazuje sve kazne za određeno vozilo, ili kazne u određenom vremenskom
     * rasponu ako su zadani odgovarajući upitni parametri.
     *
     * @param id        id vozila
     * @param odVremena početno vrijeme raspona
     * @param doVremena završno vrijeme raspona
     */
    @GET
    @Path("vozilo/{id}")
    @View("kazne.jsp")
    public void ispisKazniZaVozilo(@PathParam("id") String id,
        @QueryParam("odVremena") Long odVremena, @QueryParam("doVremena") Long doVremena) {
        List<Kazna> kazne;
        if (odVremena != null && doVremena != null) {
            kazne = restKlijentKazne.dohvatiKazneVozilaURasponu(id, odVremena, doVremena);
        } else {
            kazne = restKlijentKazne.dohvatiKazneVozila(id);
        }

        model.put("searchActionUrl", zahtjev.getContextPath() + "/mvc/kazne/vozilo/" + id);
        model.put("kazne", kazne);
    }

    /**
     * Pretražuje kazne za određeno vozilo prema id-u iz forme.
     *
     * @param id id vozila
     * @return redirekt na stranicu kazni za vozilo
     */
    @POST
    @Path("pretrazi-po-vozilu")
    public String pretrazivanjeKazniZaVozilo(@FormParam("id") String id) {
        List<Kazna> kazne = restKlijentKazne.dohvatiKazneVozila(id);
        model.put("kazne", kazne);

        return "redirect:" + "/kazne/vozilo/" + id;
    }

    /**
     * Pretražuje kaznu prema rednom broju iz forme.
     *
     * @param rb redni broj kazne
     * @return redirekt na stranicu kazne
     */
    @POST
    @Path("pretrazi-po-rednom-broju")
    public String pretrazivanjeKazne(@FormParam("rb") String rb) {
        Kazna kazna = restKlijentKazne.dohvatiKaznu(rb);
        model.put("kazna", kazna);

        return "redirect:" + "/kazne/" + rb;
    }
}
