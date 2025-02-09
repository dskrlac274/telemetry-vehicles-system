package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.kontroler;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.model.RestKlijentRadari;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.model.RestKlijentVozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Vozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.VoziloZrno;
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
@Path("vozila")
@RequestScoped
public class VozilaKontroler {
    @Context
    private ServletContext context;

    private RestKlijentVozila restKlijentVozila;

    @PostConstruct
    private void pripremiResurse() {
        try {
            String vozilaBaseUri = (String) context.getAttribute("webservis.vozila.baseuri");
            restKlijentVozila = new RestKlijentVozila("vozila", vozilaBaseUri);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Inject
    private Models model;

    @Context
    private HttpServletRequest zahtjev;

    @Inject
    private BindingResult rezultatiBindinga;

    /**
     * Prikazuje sve vožnje u zadanom vremenskom rasponu ako su zadani odgovarajući
     * upitni parametri.
     *
     * @param odVremena početno vrijeme raspona
     * @param doVremena završno vrijeme raspona
     */
    @GET
    @View("vozila.jsp")
    public void ispisVoznji(@QueryParam("odVremena") Long odVremena,
        @QueryParam("doVremena") Long doVremena) {
        if (odVremena != null && doVremena != null) {
            List<Vozila> vozila = restKlijentVozila.dohvatiVozilaURasponu(odVremena, doVremena);
            model.put("vozila", vozila);
        }
        model.put("searchActionUrl", zahtjev.getContextPath() + "/mvc/vozila/");
        model.put("kreiranjeVozilaUrl", zahtjev.getContextPath() + "/mvc/vozila");
    }

    /**
     * Prikazuje sve vožnje određenog vozila u zadanom vremenskom rasponu ako su
     * zadani odgovarajući upitni parametri.
     *
     * @param id        id vozila
     * @param odVremena početno vrijeme raspona
     * @param doVremena završno vrijeme raspona
     */
    @GET
    @Path("{id}")
    @View("vozila.jsp")
    public void ispisVoznjiVozila(@PathParam("id") String id,
        @QueryParam("odVremena") Long odVremena, @QueryParam("doVremena") Long doVremena) {
        List<Vozila> vozila;

        if (odVremena != null && doVremena != null) {
            vozila = restKlijentVozila.dohvatiVoznjeVozilaURasponu(id, odVremena, doVremena);
        } else {
            vozila = restKlijentVozila.dohvatiVoznjeVozila(id);
        }

        model.put("vozila", vozila);
        model.put("searchActionUrl", zahtjev.getContextPath() + "/mvc/vozila/" + id);
        model.put("kreiranjeVozilaUrl", zahtjev.getContextPath() + "/mvc/vozila");
    }

    /**
     * Pokreće praćenje vožnje vozila.
     *
     * @param id id vozila
     * @return redirekt na stranicu vozila
     */
    @POST
    @Path("/pracenje")
    public String pracenjeVozila(@FormParam("idVozila") String id) {
        String status = restKlijentVozila.pokreniPracenje(id);
        HttpSession session = zahtjev.getSession();
        session.setAttribute("status", status);

        return "redirect:" + "/vozila/" + id;
    }

    /**
     * Zaustavlja praćenje vožnje vozila.
     *
     * @param id id vozila
     * @return redirekt na stranicu vozila
     */
    @POST
    @Path("/zaustavljanje")
    public String zaustavljanjeVozila(@FormParam("idVozila") String id) {
        String status = restKlijentVozila.zaustaviPracenje(id);
        HttpSession session = zahtjev.getSession();
        session.setAttribute("status", status);

        return "redirect:" + "/vozila/" + id;
    }

    /**
     * Pretražuje vožnje određenog vozila prema id-u iz forme.
     *
     * @param id id vozila
     * @return redirekt na stranicu vozila za vozilo s danim id-om
     */
    @POST
    @Path("pretrazi-po-vozilu")
    public String pretrazivanjeVoznjiVozila(@FormParam("id") String id) {
        List<Vozila> vozila = restKlijentVozila.dohvatiVoznjeVozila(id);
        model.put("vozila", vozila);

        return "redirect:" + "/vozila/" + id;
    }

    /**
     * Kreira novo vozilo na temelju podataka iz forme.
     *
     * @param voziloZrno objekt koji sadrži podatke o vozilu
     * @return redirekt na stranicu vozila
     */
    @POST
    public String kreirajVozilo(@BeanParam VoziloZrno voziloZrno) {
        HttpSession session = zahtjev.getSession();

        if (rezultatiBindinga.isFailed()) {
            StringBuilder pogreskePoruke = new StringBuilder();
            for (String poruka : rezultatiBindinga.getAllMessages()) {
                pogreskePoruke.append(poruka).append("<br>");
            }
            session.setAttribute("statusKreiranjaVozila", pogreskePoruke.toString());
            return "redirect:" + "/vozila";
        }

        Vozila novoVozilo = new Vozila(voziloZrno.getId(), voziloZrno.getBroj(),
            voziloZrno.getVrijeme(), voziloZrno.getBrzina(), voziloZrno.getSnaga(),
            voziloZrno.getStruja(), voziloZrno.getVisina(), voziloZrno.getGpsBrzina(),
            voziloZrno.getTempVozila(), voziloZrno.getPostotakBaterija(),
            voziloZrno.getNaponBaterija(), voziloZrno.getKapacitetBaterija(),
            voziloZrno.getTempBaterija(), voziloZrno.getPreostaloKm(), voziloZrno.getUkupnoKm(),
            voziloZrno.getGpsSirina(), voziloZrno.getGpsDuzina());

        var odgovor = restKlijentVozila.dodajVoznjuVozila(novoVozilo);

        session.setAttribute("statusKreiranjaVozila", odgovor);

        return "redirect:" + "/vozila/" + voziloZrno.getId();
    }
}
