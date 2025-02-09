/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author daniel
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Vozila.findAll", query = "SELECT v FROM Vozila v")})
public class Vozila implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    private Integer vozilo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    private String proizvodac;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    private String model;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id", fetch = FetchType.EAGER)
    private List<Pracenevoznje> pracenevoznjeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id", fetch = FetchType.EAGER)
    private List<Kazne> kazneList;
    @JoinColumn(name = "VLASNIK", referencedColumnName = "KORISNIK")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Korisnici vlasnik;

    public Vozila() {
    }

    public Vozila(Integer vozilo) {
        this.vozilo = vozilo;
    }

    public Vozila(Integer vozilo, String proizvodac, String model) {
        this.vozilo = vozilo;
        this.proizvodac = proizvodac;
        this.model = model;
    }

    public Integer getVozilo() {
        return vozilo;
    }

    public void setVozilo(Integer vozilo) {
        this.vozilo = vozilo;
    }

    public String getProizvodac() {
        return proizvodac;
    }

    public void setProizvodac(String proizvodac) {
        this.proizvodac = proizvodac;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Pracenevoznje> getPracenevoznjeList() {
        return pracenevoznjeList;
    }

    public void setPracenevoznjeList(List<Pracenevoznje> pracenevoznjeList) {
        this.pracenevoznjeList = pracenevoznjeList;
    }

    public List<Kazne> getKazneList() {
        return kazneList;
    }

    public void setKazneList(List<Kazne> kazneList) {
        this.kazneList = kazneList;
    }

    public Korisnici getVlasnik() {
        return vlasnik;
    }

    public void setVlasnik(Korisnici vlasnik) {
        this.vlasnik = vlasnik;
    }
}
