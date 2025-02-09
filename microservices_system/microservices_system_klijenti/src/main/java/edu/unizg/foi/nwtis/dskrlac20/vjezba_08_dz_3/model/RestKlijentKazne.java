package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.model;

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

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Kazna;

public class RestKlijentKazne {
    private final WebTarget webPredlozak;

    public RestKlijentKazne(String baseUri) {
        Client klijent = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS).build();
        webPredlozak = klijent.target(baseUri).path("nwtis/v1/api/kazne");
    }

    /**
     * Dohvaća sve kazne iz REST servisa.
     *
     * @return lista kazni
     */
    public List<Kazna> dohvatiKazne() {
        Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);
        List<Kazna> kazne = new ArrayList<>();

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String odgovor = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    Kazna[] pkazne = jb.fromJson(odgovor, Kazna[].class);
                    kazne.addAll(Arrays.asList(pkazne));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return kazne;
    }

    /**
     * Dohvaća određenu kaznu prema rednom broju.
     *
     * @param rb redni broj kazne
     * @return kazna
     */
    public Kazna dohvatiKaznu(String rb) {
        WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("{0}", rb));
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String odgovor = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    return jb.fromJson(odgovor, Kazna.class);
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    /**
     * Dohvaća kazne u zadanom vremenskom rasponu.
     *
     * @param odVremena početno vrijeme raspona
     * @param doVremena završno vrijeme raspona
     * @return lista kazni
     */
    public List<Kazna> dohvatiKazneURasponu(long odVremena, long doVremena) {
        WebTarget predlozak = webPredlozak.queryParam("od", odVremena).queryParam("do", doVremena);
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);
        List<Kazna> kazne = new ArrayList<>();

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String odgovor = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    Kazna[] pkazne = jb.fromJson(odgovor, Kazna[].class);
                    kazne.addAll(Arrays.asList(pkazne));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return kazne;
    }

    /**
     * Dohvaća kazne za određeno vozilo prema id-u.
     *
     * @param id id vozila
     * @return lista kazni
     */
    public List<Kazna> dohvatiKazneVozila(String id) {
        WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("vozilo/{0}", id));
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);
        List<Kazna> kazne = new ArrayList<>();

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String odgovor = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    Kazna[] pkazne = jb.fromJson(odgovor, Kazna[].class);
                    kazne.addAll(Arrays.asList(pkazne));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return kazne;
    }

    /**
     * Dohvaća kazne za određeno vozilo u zadanom vremenskom rasponu.
     *
     * @param id        id vozila
     * @param odVremena početno vrijeme raspona
     * @param doVremena završno vrijeme raspona
     * @return lista kazni
     */
    public List<Kazna> dohvatiKazneVozilaURasponu(String id, long odVremena, long doVremena) {
        WebTarget predlozak = webPredlozak.path(java.text.MessageFormat.format("vozilo/{0}", id))
            .queryParam("od", odVremena).queryParam("do", doVremena);
        Invocation.Builder zahtjev = predlozak.request(MediaType.APPLICATION_JSON);
        List<Kazna> kazne = new ArrayList<>();

        try (Response restOdgovor = zahtjev.get()) {
            if (restOdgovor.getStatus() == 200) {
                String odgovor = restOdgovor.readEntity(String.class);
                try (Jsonb jb = JsonbBuilder.create()) {
                    Kazna[] pkazne = jb.fromJson(odgovor, Kazna[].class);
                    kazne.addAll(Arrays.asList(pkazne));
                }
            }
        } catch (Exception e) {
            return null;
        }

        return kazne;
    }

    /**
     * Provjerava stanje poslužitelja za kazne.
     *
     * @return string s informacijom o stanju poslužitelja
     */
    public String provjeriStanjePosluziteljaKazni() {
        Invocation.Builder zahtjev = webPredlozak.request(MediaType.APPLICATION_JSON);
        try (Response restOdgovor = zahtjev.head()) {
            return restOdgovor.getStatus() == 200 ? "PosluziteljKazni je aktivan."
                : "PosluziteljKazni nije aktivan.";
        } catch (Exception e) {
            return "REST servis nije dostupan.";
        }
    }
}
