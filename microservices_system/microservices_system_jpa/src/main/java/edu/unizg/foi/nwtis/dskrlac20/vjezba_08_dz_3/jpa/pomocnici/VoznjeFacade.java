package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.pomocnici;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Voznje;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Voznje_;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Klasa za upravljanje voznjama
 *
 * @author dskrlac20
 */
@RequestScoped
public class VoznjeFacade {
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
   * Sprema Voznje entitet.
   *
   * @param Voznje Voznje entitet koji se sprema
   */
  public void create(Voznje Voznje) {
    em.persist(Voznje);
  }

  /**
   * Dodaje Voznje entitet unutar transakcije.
   *
   * @param Voznje Voznje entitet koji se dodaje
   * @return true ako je operacija uspješna, false inače
   */
  @Transactional(Transactional.TxType.REQUIRED)
  public boolean dodajVoznju(Voznje Voznje) {
    try {
      create(Voznje);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * Dohvaća Voznje entitete u određenom vremenskom rasponu.
   *
   * @param odVremena početak vremenskog raspona
   * @param doVremena kraj vremenskog raspona
   * @return popis Voznje entiteta u navedenom vremenskom rasponu
   */
  public List<Voznje> dohvatiVoznjeURasponu(Long odVremena, Long doVremena) {
    CriteriaQuery<Voznje> cq = cb.createQuery(Voznje.class);
    Root<Voznje> kazne = cq.from(Voznje.class);
    cq.where(cb.between(kazne.get(Voznje_.vrijeme), odVremena, doVremena));
    TypedQuery<Voznje> q = em.createQuery(cq);
    return q.getResultList();
  }

  /**
   * Dohvaća Voznje entitete povezane s određenim Vozila entitetom.
   *
   * @param id ID Vozila entiteta
   * @return popis Voznje entiteta povezanih s navedenim Vozila entitetom
   */
  public List<Voznje> dohvatiVoznjeVozila(long id) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Voznje> cq = cb.createQuery(Voznje.class);
    Root<Voznje> root = cq.from(Voznje.class);
    cq.where(cb.equal(root.get(Voznje_.id), id));
    TypedQuery<Voznje> query = em.createQuery(cq);
    return query.getResultList();
  }

  /**
   * Dohvaća Voznje entitete povezane s određenim Vozila entitetom u vremenskom rasponu.
   *
   * @param id ID Vozila entiteta
   * @param odVremena početak vremenskog raspona
   * @param doVremena kraj vremenskog raspona
   * @return popis Voznje entiteta povezanih s navedenim Vozila entitetom u vremenskom rasponu
   */
  public List<Voznje> dohvatiVoznjeVozilaURasponu(int id, Long odVremena, Long doVremena) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Voznje> cq = cb.createQuery(Voznje.class);
    Root<Voznje> root = cq.from(Voznje.class);
    cq.where(
        cb.equal(root.get(Voznje_.id), id),
        cb.between(root.get(Voznje_.vrijeme), odVremena, doVremena)
    );
    TypedQuery<Voznje> query = em.createQuery(cq);
    return query.getResultList();
  }
}
