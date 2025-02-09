/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti;

import jakarta.json.bind.annotation.JsonbTransient;
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
    @NamedQuery(name = "Kazne.findAll", query = "SELECT k FROM Kazne k")})
public class Kazne implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "KAZNE_RB_GENERATOR", sequenceName = "KAZNE_RB_GENERATOR",
        initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "KAZNE_RB_GENERATOR")
    @Basic(optional = false)
    private Integer rb;
    @Basic(optional = false)
    @NotNull
    private long vrijemepocetak;
    @Basic(optional = false)
    @NotNull
    private long vrijemekraj;
    @Basic(optional = false)
    @NotNull
    private double brzina;
    @Basic(optional = false)
    @NotNull
    private double gpssirina;
    @Basic(optional = false)
    @NotNull
    private double gpsduzina;
    @Basic(optional = false)
    @NotNull
    private double gpssirinaradar;
    @Basic(optional = false)
    @NotNull
    private double gpsduzinaradar;
    @JoinColumn(name = "ID", referencedColumnName = "VOZILO")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Vozila id;

    public Kazne() {
    }

    public Kazne(Integer rb) {
        this.rb = rb;
    }

    public Kazne(Integer rb, long vrijemepocetak, long vrijemekraj, double brzina, double gpssirina, double gpsduzina, double gpssirinaradar, double gpsduzinaradar) {
        this.rb = rb;
        this.vrijemepocetak = vrijemepocetak;
        this.vrijemekraj = vrijemekraj;
        this.brzina = brzina;
        this.gpssirina = gpssirina;
        this.gpsduzina = gpsduzina;
        this.gpssirinaradar = gpssirinaradar;
        this.gpsduzinaradar = gpsduzinaradar;
    }

    public Integer getRb() {
        return rb;
    }

    public void setRb(Integer rb) {
        this.rb = rb;
    }

    public long getVrijemepocetak() {
        return vrijemepocetak;
    }

    public void setVrijemepocetak(long vrijemepocetak) {
        this.vrijemepocetak = vrijemepocetak;
    }

    public long getVrijemekraj() {
        return vrijemekraj;
    }

    public void setVrijemekraj(long vrijemekraj) {
        this.vrijemekraj = vrijemekraj;
    }

    public double getBrzina() {
        return brzina;
    }

    public void setBrzina(double brzina) {
        this.brzina = brzina;
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

    public double getGpssirinaradar() {
        return gpssirinaradar;
    }

    public void setGpssirinaradar(double gpssirinaradar) {
        this.gpssirinaradar = gpssirinaradar;
    }

    public double getGpsduzinaradar() {
        return gpsduzinaradar;
    }

    public void setGpsduzinaradar(double gpsduzinaradar) {
        this.gpsduzinaradar = gpsduzinaradar;
    }

    public Vozila getId() {
        return id;
    }

    public void setId(Vozila id) {
        this.id = id;
    } 
}
