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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    @NamedQuery(name = "Korisnici.findAll", query = "SELECT k FROM Korisnici k")})
public class Korisnici implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    private String korisnik;
    @Size(max = 25)
    private String ime;
    @Size(max = 25)
    private String prezime;
    @Size(max = 20)
    private String lozinka;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 100)
    private String email;
    @JoinTable(name = "ULOGE", joinColumns = {
        @JoinColumn(name = "KORISNIK", referencedColumnName = "KORISNIK")}, inverseJoinColumns = {
        @JoinColumn(name = "GRUPA", referencedColumnName = "GRUPA")})
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Grupe> grupeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vlasnik", fetch = FetchType.EAGER)
    private List<Vozila> vozilaList;

    public Korisnici() {
    }

    public Korisnici(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Grupe> getGrupeList() {
        return grupeList;
    }

    public void setGrupeList(List<Grupe> grupeList) {
        this.grupeList = grupeList;
    }

    public List<Vozila> getVozilaList() {
        return vozilaList;
    }

    public void setVozilaList(List<Vozila> vozilaList) {
        this.vozilaList = vozilaList;
    }
}
