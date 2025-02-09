package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.model;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Vozila;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RestKlijentVozila {
    private final WebTarget webPredlozak;

    public RestKlijentVozila(String vozila, String baseUri) {
        Client klijent = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS).build();
        webPredlozak = klijent.target(baseUri).path("nwtis/v1/api/" + vozila);
    }

    /**
     * Dohvaća vožnje u zadanom vremenskom rasponu.
     *
     * @param odVremena početno vrijeme raspona
     * @param doVremena završno vrijeme raspona
     * @return lista vožnji
     */
    public List<Vozila> dohvatiVozilaURasponu(long odVremena, long doVremena) {
        WebTarget predlozak = webPredlozak.queryParam("od", odVremena).queryParam("do", doVremena);
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);
        List<Vozila> vozila = new ArrayList<>();

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String json = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    Vozila[] pVozila = jb.fromJson(json, Vozila[].class);
                    vozila.addAll(Arrays.asList(pVozila));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return vozila;
    }

    /**
     * Dohvaća vožnje određenog vozila prema id-u.
     *
     * @param id id vozila
     * @return lista vožnji
     */
    public List<Vozila> dohvatiVoznjeVozila(String id) {
        WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("vozilo/{0}", id));
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);
        List<Vozila> vozila = new ArrayList<>();

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String json = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    Vozila[] pVozila = jb.fromJson(json, Vozila[].class);
                    vozila.addAll(Arrays.asList(pVozila));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return vozila;
    }

    /**
     * Dohvaća vožnje određenog vozila u zadanom vremenskom rasponu.
     *
     * @param id        id vozila
     * @param odVremena početno vrijeme raspona
     * @param doVremena završno vrijeme raspona
     * @return lista vožnji
     */
    public List<Vozila> dohvatiVoznjeVozilaURasponu(String id, long odVremena, long doVremena) {
        WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("vozilo/{0}", id))
            .queryParam("od", odVremena).queryParam("do", doVremena);
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);
        List<Vozila> vozila = new ArrayList<>();

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String json = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    Vozila[] pVozila = jb.fromJson(json, Vozila[].class);
                    vozila.addAll(Arrays.asList(pVozila));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return vozila;
    }

    /**
     * Pokreće praćenje vožnje određenog vozila.
     *
     * @param id id vozila
     * @return string s informacijom o rezultatu pokretanja praćenja
     */
    public String pokreniPracenje(String id) {
        WebTarget predlozak = webPredlozak
            .path(java.text.MessageFormat.format("vozilo/{0}/start", id));
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                return restOdgovor.readEntity(String.class);
            }
        } catch (Exception ignored) {
            return "REST servis nije dostupan.";
        }

        return "REST servis nije dostupan.";
    }

    /**
     * Zaustavlja praćenje vožnje određenog vozila.
     *
     * @param id id vozila
     * @return string s informacijom o rezultatu zaustavljanja praćenja
     */
    public String zaustaviPracenje(String id) {
        WebTarget predlozak = webPredlozak
            .path(java.text.MessageFormat.format("vozilo/{0}/stop", id));
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                return restOdgovor.readEntity(String.class);
            }
        } catch (Exception ignored) {
            return "REST servis nije dostupan.";
        }

        return "REST servis nije dostupan.";
    }

    /**
     * Dodaje novu vožnju vozila putem REST servisa.
     *
     * @param vozilo objekt vožnje vozila
     * @return string s informacijom o rezultatu dodavanja vožnje
     */
    public String dodajVoznjuVozila(Vozila vozilo) {
        Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);

        try (Response restOdgovor = zahtjev
            .post(Entity.entity(vozilo, MediaType.APPLICATION_JSON))) {
            if (restOdgovor.getStatus() == 200) {
                return restOdgovor.readEntity(String.class);
            }
        } catch (Exception ignored) {
            return "REST servis nije dostupan.";
        }

        return "REST servis nije dostupan.";
    }
}
