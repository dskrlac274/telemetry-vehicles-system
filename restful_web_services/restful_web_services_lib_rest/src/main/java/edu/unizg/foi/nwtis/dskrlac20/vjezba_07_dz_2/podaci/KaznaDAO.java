package edu.unizg.foi.nwtis.dskrlac20.vjezba_07_dz_2.podaci;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa {@code KaznaDAO} omogućuje pristup i manipulaciju podacima o kaznama u
 * bazi podataka.
 */
public class KaznaDAO {
	private final Connection vezaBP;

	/**
	 * Konstruktor koji inicijalizira vezu s bazom podataka.
	 *
	 * @param vezaBP veza s bazom podataka
	 */
	public KaznaDAO(Connection vezaBP) {
		this.vezaBP = vezaBP;
	}

	/**
	 * Dohvaća sve kazne iz baze podataka.
	 *
	 * @return lista svih kazni
	 */
	public List<Kazna> dohvatiSveKazne() {
		String upit = "SELECT * FROM kazne";

		List<Kazna> kazne = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var kazna = kreirajObjektKazna(rs);
				kazne.add(kazna);
			}
		} catch (SQLException ex) {
			Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return kazne;
	}

	/**
	 * Dohvaća kaznu iz baze podataka prema rednom broju.
	 *
	 * @param rb redni broj kazne
	 * @return objekt {@code Kazna} ako postoji, inače null
	 */
	public Kazna dohvatiKaznu(int rb) {
		String upit = "SELECT * FROM kazne WHERE rb = ?";

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setInt(1, rb);
			ResultSet rs = s.executeQuery();

			return rs.next() ? kreirajObjektKazna(rs) : null;
		} catch (SQLException ex) {
			Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Dohvaća kazne u zadanom vremenskom rasponu.
	 *
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista kazni u zadanom vremenskom rasponu
	 */
	public List<Kazna> dohvatiKazneURasponu(long odVremena, long doVremena) {
		String upit = "SELECT * FROM kazne WHERE vrijemeKraj >= ? AND vrijemeKraj <= ?";

		List<Kazna> kazne = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setLong(1, odVremena);
			s.setLong(2, doVremena);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var kazna = kreirajObjektKazna(rs);
				kazne.add(kazna);
			}
		} catch (SQLException ex) {
			Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return kazne;
	}

	/**
	 * Dohvaća kazne određenog vozila prema ID-u vozila.
	 *
	 * @param id ID vozila
	 * @return lista kazni određenog vozila
	 */
	public List<Kazna> dohvatiKazneVozila(int id) {
		String upit = "SELECT * FROM kazne WHERE id = ?";

		List<Kazna> kazne = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setInt(1, id);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var kazna = kreirajObjektKazna(rs);
				kazne.add(kazna);
			}
		} catch (SQLException ex) {
			Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return kazne;
	}

	/**
	 * Dohvaća kazne određenog vozila u zadanom vremenskom rasponu prema id-u
	 * vozila.
	 *
	 * @param id        ID vozila
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista kazni određenog vozila u zadanom vremenskom rasponu
	 */
	public List<Kazna> dohvatiKazneVozilaURasponu(int id, long odVremena, long doVremena) {
		String upit = "SELECT * FROM kazne WHERE id = ? AND vrijemeKraj >= ? AND vrijemeKraj <= ?";

		List<Kazna> kazne = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setInt(1, id);
			s.setLong(2, odVremena);
			s.setLong(3, doVremena);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var kazna = kreirajObjektKazna(rs);
				kazne.add(kazna);
			}
		} catch (SQLException ex) {
			Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return kazne;
	}

	/**
	 * Dodaje novu kaznu u bazu podataka.
	 *
	 * @param kazna objekt kazne s podacima o kazni
	 * @return objekt kazne ako je uspješno dodano, null inače
	 */
	public Kazna dodajKaznu(Kazna kazna) {
		String upit = "INSERT INTO kazne (id, vrijemePocetak, vrijemeKraj, brzina, gpsSirina, gpsDuzina, "
				+ "gpsSirinaRadar, gpsDuzinaRadar) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {

			s.setInt(1, kazna.getId());
			s.setLong(2, kazna.getVrijemePocetak());
			s.setLong(3, kazna.getVrijemeKraj());
			s.setDouble(4, kazna.getBrzina());
			s.setDouble(5, kazna.getGpsSirina());
			s.setDouble(6, kazna.getGpsDuzina());
			s.setDouble(7, kazna.getGpsSirinaRadara());
			s.setDouble(8, kazna.getGpsDuzinaRadara());

			return s.executeUpdate() == 1 ? kazna : null;

		} catch (Exception ex) {
			Logger.getLogger(KaznaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Kreira i vraća objekt {@code Kazna} iz rezultata SQL upita.
	 *
	 * @param rs rezultat SQL upita
	 * @return objekt {@code Kazna} s podacima iz rezultata upita
	 * @throws SQLException ako dođe do greške prilikom čitanja podataka iz
	 *                      rezultata upita
	 */
	private Kazna kreirajObjektKazna(ResultSet rs) throws SQLException {
		int rb = rs.getInt("rb");
		int id = rs.getInt("id");
		long vrijemePocetak = rs.getLong("vrijemePocetak");
		long vrijemeKraj = rs.getLong("vrijemeKraj");
		double brzina = rs.getDouble("brzina");
		double gpsSirina = rs.getDouble("gpsSirina");
		double gpsDuzina = rs.getDouble("gpsDuzina");
		double gpsSirinaRadar = rs.getDouble("gpsSirinaRadar");
		double gpsDuzinaRadar = rs.getDouble("gpsDuzinaRadar");

		return new Kazna(rb, id, vrijemePocetak, vrijemeKraj, brzina, gpsSirina, gpsDuzina,
				gpsSirinaRadar, gpsDuzinaRadar);
	}
}
