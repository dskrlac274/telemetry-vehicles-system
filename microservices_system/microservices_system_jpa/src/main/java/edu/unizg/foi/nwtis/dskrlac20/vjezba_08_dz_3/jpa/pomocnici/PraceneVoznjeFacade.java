package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.pomocnici;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Pracenevoznje;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Pracenevoznje_;
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
 * Klasa za upravljanje pracenim voznjama
 *
 * @author dskrlac20
 */
@RequestScoped
public class PraceneVoznjeFacade {

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
   * Sprema Pracenevoznje entitet.
   *
   * @param pracenevoznje Pracenevoznje entitet koji se sprema
   */
  public void create(Pracenevoznje pracenevoznje) {
    em.persist(pracenevoznje);
  }

  /**
   * Dodaje Pracenevoznje entitet unutar transakcije.
   *
   * @param pracenevoznje Pracenevoznje entitet koji se dodaje
   * @return true ako je operacija uspješna, false inače
   */
  @Transactional(Transactional.TxType.REQUIRED)
  public boolean dodajVoznju(Pracenevoznje pracenevoznje) {
    try {
      create(pracenevoznje);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * Dohvaća Pracenevoznje entitete u određenom vremenskom rasponu.
   *
   * @param odVremena početak vremenskog raspona
   * @param doVremena kraj vremenskog raspona
   * @return popis Pracenevoznje entiteta u navedenom vremenskom rasponu
   */
  public List<Pracenevoznje> dohvatiVoznjeURasponu(Long odVremena, Long doVremena) {
    CriteriaQuery<Pracenevoznje> cq = cb.createQuery(Pracenevoznje.class);
    Root<Pracenevoznje> kazne = cq.from(Pracenevoznje.class);
    cq.where(cb.between(kazne.get(Pracenevoznje_.vrijeme), odVremena, doVremena));
    TypedQuery<Pracenevoznje> q = em.createQuery(cq);
    return q.getResultList();
  }

  /**
   * Dohvaća Pracenevoznje entitete povezane s određenim Vozila entitetom.
   *
   * @param id ID Vozila entiteta
   * @return popis Pracenevoznje entiteta povezanih s navedenim Vozila entitetom
   */
  public List<Pracenevoznje> dohvatiVoznjeVozila(long id) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Pracenevoznje> cq = cb.createQuery(Pracenevoznje.class);
    Root<Pracenevoznje> kazne = cq.from(Pracenevoznje.class);
    Join<Pracenevoznje, Vozila> vozila = kazne.join(Pracenevoznje_.id);
    cq.where(cb.equal(vozila.get(Vozila_.vozilo), id));
    TypedQuery<Pracenevoznje> q = em.createQuery(cq);
    return q.getResultList();
  }

  /**
   * Dohvaća Pracenevoznje entitete povezane s određenim Vozila entitetom u vremenskom rasponu.
   *
   * @param id ID Vozila entiteta
   * @param odVremena početak vremenskog raspona
   * @param doVremena kraj vremenskog raspona
   * @return popis Pracenevoznje entiteta povezanih s navedenim Vozila entitetom u vremenskom rasponu
   */
  public List<Pracenevoznje> dohvatiVoznjeVozilaURasponu(int id, Long odVremena, Long doVremena) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Pracenevoznje> cq = cb.createQuery(Pracenevoznje.class);
    Root<Pracenevoznje> kazne = cq.from(Pracenevoznje.class);
    Join<Pracenevoznje, Vozila> vozila = kazne.join(Pracenevoznje_.id);
    cq.where(cb.and(cb.equal(vozila.get(Vozila_.vozilo), id),
        cb.between(kazne.get(Pracenevoznje_.vrijeme), odVremena, doVremena)));
    TypedQuery<Pracenevoznje> q = em.createQuery(cq);
    return q.getResultList();
  }
}
