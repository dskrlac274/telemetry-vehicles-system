/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.pomocnici;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jpa.entiteti.Vozila;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Klasa za upravljanje vozilima
 *
 * @author dskrlac20
 */
@RequestScoped
public class VozilaFacade {
	@PersistenceContext(unitName = "nwtis_pu")
	private EntityManager em;

	/**
	 * Inicijalizira facade.
	 */
	@PostConstruct
	private void init() {
	}

	/**
	 * Sprema Vozila entitet.
	 *
	 * @param vozila Vozila entitet koji se sprema
	 */
	public void create(Vozila vozila) {
		em.persist(vozila);
	}

	/**
	 * Ažurira Vozila entitet.
	 *
	 * @param vozila Vozila entitet koji se ažurira
	 */
	public void edit(Vozila vozila) {
		em.merge(vozila);
	}

	/**
	 * Briše Vozila entitet.
	 *
	 * @param vozila Vozila entitet koji se briše
	 */
	public void remove(Vozila vozila) {
		em.remove(em.merge(vozila));
	}

	/**
	 * Pronalazi Vozila entitet prema primarnom ključu.
	 *
	 * @param id primarni ključ Vozila entiteta
	 * @return pronađeni Vozila entitet
	 */
	public Vozila find(Object id) {
		return em.find(Vozila.class, id);
	}
}
