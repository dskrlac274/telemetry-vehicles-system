/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @author daniel
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Pracenevoznje.findAll", query = "SELECT p FROM Pracenevoznje p")})
public class Pracenevoznje implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "PRACENEVOZNJE_RB_GENERATOR",
        sequenceName = "PRACENEVOZNJE_RB_GENERATOR", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRACENEVOZNJE_RB_GENERATOR")
    @Basic(optional = false)
    private Integer rb;
    @Basic(optional = false)
    @NotNull
    private int broj;
    @Basic(optional = false)
    @NotNull
    private long vrijeme;
    @Basic(optional = false)
    @NotNull
    private double brzina;
    @Basic(optional = false)
    @NotNull
    private double snaga;
    @Basic(optional = false)
    @NotNull
    private double struja;
    @Basic(optional = false)
    @NotNull
    private double visina;
    @Basic(optional = false)
    @NotNull
    private double gpsbrzina;
    @Basic(optional = false)
    @NotNull
    private int tempvozila;
    @Basic(optional = false)
    @NotNull
    private int postotakbaterija;
    @Basic(optional = false)
    @NotNull
    private double naponbaterija;
    @Basic(optional = false)
    @NotNull
    private int kapacitetbaterija;
    @Basic(optional = false)
    @NotNull
    private int tempbaterija;
    @Basic(optional = false)
    @NotNull
    private double preostalokm;
    @Basic(optional = false)
    @NotNull
    private double ukupnokm;
    @Basic(optional = false)
    @NotNull
    private double gpssirina;
    @Basic(optional = false)
    @NotNull
    private double gpsduzina;
    @JoinColumn(name = "ID", referencedColumnName = "VOZILO")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Vozila id;

    public Pracenevoznje() {
    }

    public Pracenevoznje(Integer rb) {
        this.rb = rb;
    }

    public Pracenevoznje(Integer rb, int broj, long vrijeme, double brzina, double snaga, double struja, double visina, double gpsbrzina, int tempvozila, int postotakbaterija, double naponbaterija, int kapacitetbaterija, int tempbaterija, double preostalokm, double ukupnokm, double gpssirina, double gpsduzina) {
        this.rb = rb;
        this.broj = broj;
        this.vrijeme = vrijeme;
        this.brzina = brzina;
        this.snaga = snaga;
        this.struja = struja;
        this.visina = visina;
        this.gpsbrzina = gpsbrzina;
        this.tempvozila = tempvozila;
        this.postotakbaterija = postotakbaterija;
        this.naponbaterija = naponbaterija;
        this.kapacitetbaterija = kapacitetbaterija;
        this.tempbaterija = tempbaterija;
        this.preostalokm = preostalokm;
        this.ukupnokm = ukupnokm;
        this.gpssirina = gpssirina;
        this.gpsduzina = gpsduzina;
    }

    public Integer getRb() {
        return rb;
    }

    public void setRb(Integer rb) {
        this.rb = rb;
    }

    public int getBroj() {
        return broj;
    }

    public void setBroj(int broj) {
        this.broj = broj;
    }

    public long getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(long vrijeme) {
        this.vrijeme = vrijeme;
    }

    public double getBrzina() {
        return brzina;
    }

    public void setBrzina(double brzina) {
        this.brzina = brzina;
    }

    public double getSnaga() {
        return snaga;
    }

    public void setSnaga(double snaga) {
        this.snaga = snaga;
    }

    public double getStruja() {
        return struja;
    }

    public void setStruja(double struja) {
        this.struja = struja;
    }

    public double getVisina() {
        return visina;
    }

    public void setVisina(double visina) {
        this.visina = visina;
    }

    public double getGpsbrzina() {
        return gpsbrzina;
    }

    public void setGpsbrzina(double gpsbrzina) {
        this.gpsbrzina = gpsbrzina;
    }

    public int getTempvozila() {
        return tempvozila;
    }

    public void setTempvozila(int tempvozila) {
        this.tempvozila = tempvozila;
    }

    public int getPostotakbaterija() {
        return postotakbaterija;
    }

    public void setPostotakbaterija(int postotakbaterija) {
        this.postotakbaterija = postotakbaterija;
    }

    public double getNaponbaterija() {
        return naponbaterija;
    }

    public void setNaponbaterija(double naponbaterija) {
        this.naponbaterija = naponbaterija;
    }

    public int getKapacitetbaterija() {
        return kapacitetbaterija;
    }

    public void setKapacitetbaterija(int kapacitetbaterija) {
        this.kapacitetbaterija = kapacitetbaterija;
    }

    public int getTempbaterija() {
        return tempbaterija;
    }

    public void setTempbaterija(int tempbaterija) {
        this.tempbaterija = tempbaterija;
    }

    public double getPreostalokm() {
        return preostalokm;
    }

    public void setPreostalokm(double preostalokm) {
        this.preostalokm = preostalokm;
    }

    public double getUkupnokm() {
        return ukupnokm;
    }

    public void setUkupnokm(double ukupnokm) {
        this.ukupnokm = ukupnokm;
    }

    public double getGpssirina() {
        return gpssirina;
    }

    public void setGpssirina(double gpssirina) {
        this.gpssirina = gpssirina;
    }

    public double getGpsduzina() {
        return gpsduzina;
    }

    public void setGpsduzina(double gpsduzina) {
        this.gpsduzina = gpsduzina;
    }

    public Vozila getId() {
        return id;
    }

    public void setId(Vozila id) {
        this.id = id;
    }
}
