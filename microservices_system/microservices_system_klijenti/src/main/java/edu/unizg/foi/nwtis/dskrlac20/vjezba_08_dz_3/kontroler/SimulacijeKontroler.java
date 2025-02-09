package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.kontroler;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.model.RestKlijentVozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.SimulacijaZrno;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@Path("simulacije")
@RequestScoped
public class SimulacijeKontroler {
    @Context
    private ServletContext context;

    private RestKlijentVozila restKlijentVozila;

    /**
     * Priprema resursa.
     */
    @PostConstruct
    private void pripremiResurse() {
        try {
            String simulacijeBaseUri = (String) context.getAttribute("webservis.simulacije.baseuri");
            restKlijentVozila = new RestKlijentVozila("simulacije", simulacijeBaseUri);
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

    private final ExecutorService izvrsnik = Executors.newSingleThreadExecutor();

    /**
     * Prikazuje sve vožnje u zadanom vremenskom rasponu ako su zadani odgovarajući
     * upitni parametri.
     *
     * @param odVremena početno vrijeme raspona
     * @param doVremena završno vrijeme raspona
     */
    @GET
    @View("simulacije.jsp")
    public void ispisVoznji(@QueryParam("odVremena") Long odVremena,
        @QueryParam("doVremena") Long doVremena) {
        if (odVremena != null && doVremena != null) {
            List<Vozila> vozila = restKlijentVozila.dohvatiVozilaURasponu(odVremena, doVremena);
            model.put("vozila", vozila);
        }
        model.put("searchActionUrl", zahtjev.getContextPath() + "/mvc/simulacije");
        model.put("kreiranjeVozilaUrl", zahtjev.getContextPath() + "/mvc/simulacije");
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
    @View("simulacije.jsp")
    public void ispisVoznjiVozila(@PathParam("id") String id,
        @QueryParam("odVremena") Long odVremena, @QueryParam("doVremena") Long doVremena) {
        List<Vozila> vozila;

        if (odVremena != null && doVremena != null) {
            vozila = restKlijentVozila.dohvatiVoznjeVozilaURasponu(id, odVremena, doVremena);
        } else {
            vozila = restKlijentVozila.dohvatiVoznjeVozila(id);
        }

        model.put("vozila", vozila);
        model.put("searchActionUrl", zahtjev.getContextPath() + "/mvc/simulacije/" + id);
        model.put("kreiranjeVozilaUrl", zahtjev.getContextPath() + "/mvc/simulacije");
    }

    /**
     * Pokreće simulaciju na temelju podataka iz datoteke.
     *
     * @param simulacijaZrno objekt koji sadrži podatke o simulaciji
     * @return redirekt na stranicu simulacija
     */
    @POST
    @Path("pokreni")
    public String pokreniSimulaciju(@BeanParam SimulacijaZrno simulacijaZrno) {
        HttpSession session = zahtjev.getSession();

        if (rezultatiBindinga.isFailed()) {
            session.setAttribute("statusKreiranjaSimulacije",
                String.join("<br>", rezultatiBindinga.getAllMessages()));
            return "redirect:/simulacije";
        }

        String putanja = zahtjev.getServletContext()
            .getRealPath("/WEB-INF/" + simulacijaZrno.getNazivDatoteke());
        izvrsnik.submit(() -> procesirajDatoteku(putanja, simulacijaZrno, session));

        session.setAttribute("simulacija", "Simulacija je uspješno započela. Možete se "
            + "slobodno navigirati po stranicama. Kada simulacija završi, biti ćete obaviješteni "
            + "nakon osvježavanja web stranice.");

        return "redirect:/simulacije/" + simulacijaZrno.getIdVozila();
    }

    private void procesirajDatoteku(String putanja, SimulacijaZrno simulacijaZrno,
        HttpSession session) {
        int brojRedka = 1;
        long prethodnoVrijeme = 0;
        boolean prviRedak = true;

        try (BufferedReader citac = new BufferedReader(new FileReader(putanja))) {
            citac.readLine();
            String linija;
            while ((linija = citac.readLine()) != null) {
                if (prviRedak) {
                    prethodnoVrijeme = Long.parseLong(linija.split(",")[0]);
                    prviRedak = false;
                }
                prethodnoVrijeme = obradiLiniju(linija, brojRedka, prethodnoVrijeme,
                    simulacijaZrno.getTrajanjeSekunde(), simulacijaZrno.getIdVozila());
                brojRedka++;
                Thread.sleep(simulacijaZrno.getTrajanjePauze());
            }
            session.setAttribute("simulacija", "Slanje podataka simulacije je završeno.");
        } catch (Exception e) {
            session.setAttribute("simulacija", "Greška pri obradi datoteke simulacije.");
        }
    }

    /**
     * Pretražuje vožnje određenog vozila prema id-u iz forme.
     *
     * @param id id vozila
     * @return redirekt na stranicu simulacija za vozilo s danim id-om
     */
    @POST
    @Path("pretrazi-po-vozilu")
    public String pretrazivanjeVoznjiVozila(@FormParam("id") String id) {
        List<Vozila> vozila = restKlijentVozila.dohvatiVoznjeVozila(id);
        model.put("vozila", vozila);

        return "redirect:" + "/simulacije/" + id;
    }

    /**
     * Kreira novo vozilo na temelju podataka iz forme.
     *
     * @param voziloZrno objekt koji sadrži podatke o vozilu
     * @return redirekt na stranicu simulacija
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
            return "redirect:" + "/simulacije";
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

        return "redirect:" + "/simulacije/" + voziloZrno.getId();
    }

    /**
     * Obrađuje jednu liniju iz datoteke s podacima o vožnji.
     *
     * @param linija           linija iz datoteke
     * @param brojRedka        broj trenutne linije
     * @param prethodnoVrijeme vrijeme prethodne linije
     * @param trajanjeSekunde  trajanje jedne sekunde u simulaciji
     * @param idVozila         id vozila
     * @return trenutno vrijeme linije
     * @throws InterruptedException ako dođe do prekida pri spavanju dretve
     */
    private long obradiLiniju(String linija, int brojRedka, long prethodnoVrijeme,
        int trajanjeSekunde, int idVozila) throws InterruptedException {
        String[] dijelovi = linija.split(",");
        long trenutnoVrijeme = Long.parseLong(dijelovi[0]);
        long razlikaVremena = trenutnoVrijeme - prethodnoVrijeme;
        double korekcija = trajanjeSekunde / 1000.0;
        long pauza = (long) (razlikaVremena * korekcija);

        Thread.sleep(pauza);

        Vozila vozilo = dohvatiVozilo(idVozila, brojRedka, trenutnoVrijeme, dijelovi);

        restKlijentVozila.dodajVoznjuVozila(vozilo);

        return trenutnoVrijeme;
    }

    /**
     * Kreira objekt vozila na temelju danih podataka.
     *
     * @param idVozila        ID vozila
     * @param brojRedka       broj trenutne linije
     * @param trenutnoVrijeme vrijeme linije
     * @param dijelovi        dijelovi linije
     * @return kreirani objekt vozila
     */
    private Vozila dohvatiVozilo(int idVozila, int brojRedka, long trenutnoVrijeme,
        String[] dijelovi) {
        Vozila vozilo = new Vozila();
        vozilo.setId(idVozila);
        vozilo.setBroj(brojRedka);
        vozilo.setVrijeme(trenutnoVrijeme);
        vozilo.setBrzina(Double.parseDouble(dijelovi[1]));
        vozilo.setSnaga(Double.parseDouble(dijelovi[2]));
        vozilo.setStruja(Double.parseDouble(dijelovi[3]));
        vozilo.setVisina(Double.parseDouble(dijelovi[4]));
        vozilo.setGpsBrzina(Double.parseDouble(dijelovi[5]));
        vozilo.setTempVozila(Integer.parseInt(dijelovi[6]));
        vozilo.setPostotakBaterija(Integer.parseInt(dijelovi[7]));
        vozilo.setNaponBaterija(Double.parseDouble(dijelovi[8]));
        vozilo.setKapacitetBaterija(Integer.parseInt(dijelovi[9]));
        vozilo.setTempBaterija(Integer.parseInt(dijelovi[10]));
        vozilo.setPreostaloKm(Double.parseDouble(dijelovi[11]));
        vozilo.setUkupnoKm(Double.parseDouble(dijelovi[12]));
        vozilo.setGpsSirina(Double.parseDouble(dijelovi[13]));
        vozilo.setGpsDuzina(Double.parseDouble(dijelovi[14]));
        return vozilo;
    }
}
