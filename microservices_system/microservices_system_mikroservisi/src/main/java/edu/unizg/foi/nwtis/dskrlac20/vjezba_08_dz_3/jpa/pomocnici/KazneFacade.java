/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.pomocnici;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Kazne;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Kazne_;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Vozila;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Vozila_;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 *
 * Klasa za upravljanje kaznama
 *
 * @author dskrlac20
 */
@RequestScoped
public class KazneFacade {
  @PersistenceContext(unitName = "nwtis_pu")
  private EntityManager em;
  private CriteriaBuilder cb;

  /**
   * Inicijalizira CriteriaBuilder.
   */
  @PostConstruct
  private void init() {
    cb = em.getCriteriaBuilder();
  }

  /**
   * Sprema Kazne entitet.
   *
   * @param kazne Kazne entitet koji se sprema
   */
  public void create(Kazne kazne) {
    em.persist(kazne);
  }

  /**
   * Ažurira Kazne entitet.
   *
   * @param kazne Kazne entitet koji se ažurira
   */
  public void edit(Kazne kazne) {
    em.merge(kazne);
  }

  /**
   * Briše Kazne entitet.
   *
   * @param kazne Kazne entitet koji se briše
   */
  public void remove(Kazne kazne) {
    em.remove(em.merge(kazne));
  }

  /**
   * Pronalazi Kazne entitet prema primarnom ključu.
   *
   * @param id primarni ključ Kazne entiteta
   * @return pronađeni Kazne entitet
   */
  public Kazne find(Object id) {
    return em.find(Kazne.class, id);
  }

  /**
   * Dohvaća sve Kazne entitete.
   *
   * @return popis svih Kazne entiteta
   */
  public List<Kazne> dohvatiSveKazne() {
    CriteriaQuery<Kazne> cq = cb.createQuery(Kazne.class);
    cq.select(cq.from(Kazne.class));
    return em.createQuery(cq).getResultList();
  }

  /**
   * Dohvaća Kazne entitete u određenom vremenskom rasponu.
   *
   * @param odVremena početak vremenskog raspona
   * @param doVremena kraj vremenskog raspona
   * @return popis Kazne entiteta u navedenom vremenskom rasponu
   */
  public List<Kazne> dohvatiKazneURasponu(long odVremena, long doVremena) {
    CriteriaQuery<Kazne> cq = cb.createQuery(Kazne.class);
    Root<Kazne> kazne = cq.from(Kazne.class);
    cq.where(cb.between(kazne.get(Kazne_.vrijemekraj), odVremena, doVremena));
    TypedQuery<Kazne> q = em.createQuery(cq);
    return q.getResultList();
  }

  /**
   * Dohvaća Kazne entitet prema rb polju.
   *
   * @param rb vrijednost rb polja
   * @return Kazne entitet s navedenom rb vrijednošću
   */
  public Kazne dohvatiKaznu(long rb) {
    CriteriaQuery<Kazne> cq = cb.createQuery(Kazne.class);
    Root<Kazne> kazne = cq.from(Kazne.class);
    cq.where(cb.equal(kazne.get(Kazne_.rb), rb));
    TypedQuery<Kazne> q = em.createQuery(cq);
    return q.getSingleResult();
  }

  /**
   * Dohvaća Kazne entitete povezane s određenim Vozila entitetom.
   *
   * @param id ID Vozila entiteta
   * @return popis Kazne entiteta povezanih s navedenim Vozila entitetom
   */
  public List<Kazne> dohvatiKazneVozila(long id) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Kazne> cq = cb.createQuery(Kazne.class);
    Root<Kazne> kazne = cq.from(Kazne.class);
    Join<Kazne, Vozila> vozila = kazne.join(Kazne_.id);
    cq.where(cb.equal(vozila.get(Vozila_.vozilo), id));
    TypedQuery<Kazne> q = em.createQuery(cq);
    return q.getResultList();
  }

  /**
   * Dohvaća Kazne entitete povezane s određenim Vozila entitetom u vremenskom rasponu.
   *
   * @param id ID Vozila entiteta
   * @param odVremena početak vremenskog raspona
   * @param doVremena kraj vremenskog raspona
   * @return popis Kazne entiteta povezanih s navedenim Vozila entitetom u vremenskom rasponu
   */
  public List<Kazne> dohvatiKazneVozilaURasponu(long id, long odVremena, long doVremena) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Kazne> cq = cb.createQuery(Kazne.class);
    Root<Kazne> kazne = cq.from(Kazne.class);
    Join<Kazne, Vozila> vozila = kazne.join(Kazne_.id);
    cq.where(cb.and(cb.equal(vozila.get(Vozila_.vozilo), id),
        cb.between(kazne.get(Kazne_.vrijemekraj), odVremena, doVremena)));
    TypedQuery<Kazne> q = em.createQuery(cq);
    return q.getResultList();
  }

  /**
   * Dodaje Kazne entitet unutar transakcije.
   *
   * @param kazne Kazne entitet koji se dodaje
   * @return true ako je operacija uspješna, false inače
   */
  @Transactional(Transactional.TxType.REQUIRED)
  public boolean dodajKaznu(Kazne kazne) {
    try {
      create(kazne);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }
}
