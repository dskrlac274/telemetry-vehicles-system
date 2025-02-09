/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
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
    @NamedQuery(name = "Grupe.findAll", query = "SELECT g FROM Grupe g")})
public class Grupe implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    private String grupa;
    @Size(max = 55)
    private String naziv;
    @ManyToMany(mappedBy = "grupeList", fetch = FetchType.EAGER)
    private List<Korisnici> korisniciList;

    public Grupe() {
    }

    public Grupe(String grupa) {
        this.grupa = grupa;
    }

    public String getGrupa() {
        return grupa;
    }

    public void setGrupa(String grupa) {
        this.grupa = grupa;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public List<Korisnici> getKorisniciList() {
        return korisniciList;
    }

    public void setKorisniciList(List<Korisnici> korisniciList) {
        this.korisniciList = korisniciList;
    }
}
