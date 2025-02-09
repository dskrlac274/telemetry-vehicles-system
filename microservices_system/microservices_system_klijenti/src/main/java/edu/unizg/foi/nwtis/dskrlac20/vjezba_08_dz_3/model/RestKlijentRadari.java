package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.model;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Radar;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Klasa {@code RestKlijentRadari} omogućuje komunikaciju s RESTful web servisom
 * za upravljanje radarima.
 *
 * @author dskrlac20
 */
public class RestKlijentRadari {
    private final WebTarget webPredlozak;

    /**
     * Konstruktor koji inicijalizira REST klijenta s baznom URL adresom.
     */
    public RestKlijentRadari(String baseUri) {
        Client klijent = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS).build();
        webPredlozak = klijent.target(baseUri).path("nwtis/v1/api/radari");
    }

    /**
     * Dohvaća sve radare iz REST servisa.
     *
     * @return lista radara
     */
    public List<Radar> dohvatiRadare() {
        Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);

        List<Radar> radari = new ArrayList<>();

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String odgovor = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    Radar[] pRadari = jb.fromJson(odgovor, Radar[].class);
                    radari.addAll(Arrays.asList(pRadari));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return radari;
    }

    /**
     * Dohvaća određeni radar prema id-u.
     *
     * @param id id radara
     * @return radar
     */
    public Radar dohvatiRadar(String id) {
        WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("{0}", id));
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String odgovor = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    return jb.fromJson(odgovor, Radar.class);
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    /**
     * Provjerava stanje određenog radara prema id-u.
     *
     * @param id id radara
     * @return string s informacijom o stanju radara
     */
    public String provjeriRadar(String id) {
        WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("{0}/provjeri", id));
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

        try (Response restOdgovor = zahtjev.get()) {
            return restOdgovor.readEntity(String.class);
        } catch (Exception ignored) {
            return "REST servis nije dostupan.";
        }
    }

    /**
     * Resetira sve radare.
     *
     * @return string s informacijom o rezultatu resetiranja radara
     */
    public String resetirajRadare() {
        WebTarget predlozak = webPredlozak.path("reset");
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

        try (Response restOdgovor = zahtjev.get()) {
            return restOdgovor.readEntity(String.class);
        } catch (Exception ignored) {
            return "REST servis nije dostupan.";
        }
    }

    /**
     * Briše određeni radar prema id-u.
     *
     * @param id id radara
     * @return string s informacijom o rezultatu brisanja radara
     */
    public String obrisiRadar(String id) {
        WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("{0}", id));
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

        try (Response restOdgovor = zahtjev.delete()) {
            return restOdgovor.readEntity(String.class);
        } catch (Exception ignored) {
            return "REST servis nije dostupan.";
        }
    }

    /**
     * Briše sve radare.
     *
     * @return string s informacijom o rezultatu brisanja svih radara
     */
    public String obrisiSveRadare() {
        Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);

        try (Response restOdgovor = zahtjev.delete()) {
            return restOdgovor.readEntity(String.class);
        } catch (Exception ignored) {
            return "REST servis nije dostupan.";
        }
    }
}
