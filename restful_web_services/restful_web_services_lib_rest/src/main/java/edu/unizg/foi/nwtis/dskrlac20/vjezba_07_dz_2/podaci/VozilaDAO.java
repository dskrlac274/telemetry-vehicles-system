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
 * Klasa {@code VozilaDAO} omogućuje pristup i manipulaciju podacima o vozilima
 * u bazi podataka.
 *
 * @author dskrlac20
 */
public class VozilaDAO {
	private final Connection vezaBP;
	private final String tablica;

	/**
	 * Konstruktor koji inicijalizira vezu s bazom podataka i naziv tablice.
	 *
	 * @param vezaBP  veza s bazom podataka
	 * @param tablica naziv tablice u bazi podataka
	 */
	public VozilaDAO(Connection vezaBP, String tablica) {
		this.vezaBP = vezaBP;
		this.tablica = tablica;
	}

	/**
	 * Dohvaća vožnje vozila u zadanom vremenskom rasponu.
	 *
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista vožnji vozila u zadanom vremenskom rasponu
	 */
	public List<Vozila> dohvatiVoznjeURasponu(long odVremena, long doVremena) {
		String upit = "SELECT * FROM " + tablica + " WHERE vrijeme >= ? AND vrijeme <= ?";
		List<Vozila> voznje = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setLong(1, odVremena);
			s.setLong(2, doVremena);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var vozilo = kreirajObjektVozila(rs);
				voznje.add(vozilo);
			}
		} catch (SQLException ex) {
			Logger.getLogger(VozilaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return voznje;
	}

	/**
	 * Dohvaća vožnje određenog vozila prema njegovom Iid-u.
	 *
	 * @param id      ID vozila
	 * @param tablica naziv tablice u bazi podataka
	 * @return lista vožnji određenog vozila
	 */
	public List<Vozila> dohvatiVoznjeVozila(int id, String tablica) {
		String upit = "SELECT * FROM " + tablica + " WHERE id = ?";
		List<Vozila> voznje = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setInt(1, id);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var vozilo = kreirajObjektVozila(rs);
				voznje.add(vozilo);
			}
		} catch (SQLException ex) {
			Logger.getLogger(VozilaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return voznje;
	}

	/**
	 * Dohvaća vožnje određenog vozila u zadanom vremenskom rasponu prema id-u
	 * vozila.
	 *
	 * @param id        ID vozila
	 * @param odVremena početno vrijeme raspona
	 * @param doVremena završno vrijeme raspona
	 * @return lista vožnji određenog vozila u zadanom vremenskom rasponu
	 */
	public List<Vozila> dohvatiVoznjeVozilaURasponu(int id, long odVremena, long doVremena) {
		String upit = "SELECT * FROM " + tablica
				+ " WHERE id = ? AND vrijeme >= ? AND vrijeme <= ?";
		List<Vozila> voznje = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setInt(1, id);
			s.setLong(2, odVremena);
			s.setLong(3, doVremena);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var vozilo = kreirajObjektVozila(rs);
				voznje.add(vozilo);
			}
		} catch (SQLException ex) {
			Logger.getLogger(VozilaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return voznje;
	}

	/**
	 * Dodaje novu vožnju vozila u bazu podataka.
	 *
	 * @param vozilo objekt vozila s podacima o vožnji
	 * @return objekt vozila ako je uspješno dodano, null inače
	 */
	public Vozila dodajVoznju(Vozila vozilo) {
		String upit = "INSERT INTO " + tablica
				+ " (id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, "
				+ "tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm, "
				+ "gpsSirina, gpsDuzina) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setInt(1, vozilo.getId());
			s.setInt(2, vozilo.getBroj());
			s.setLong(3, vozilo.getVrijeme());
			s.setDouble(4, vozilo.getBrzina());
			s.setDouble(5, vozilo.getSnaga());
			s.setDouble(6, vozilo.getStruja());
			s.setDouble(7, vozilo.getVisina());
			s.setDouble(8, vozilo.getGpsBrzina());
			s.setInt(9, vozilo.getTempVozila());
			s.setInt(10, vozilo.getPostotakBaterija());
			s.setDouble(11, vozilo.getNaponBaterija());
			s.setInt(12, vozilo.getKapacitetBaterija());
			s.setInt(13, vozilo.getTempBaterija());
			s.setDouble(14, vozilo.getPreostaloKm());
			s.setDouble(15, vozilo.getUkupnoKm());
			s.setDouble(16, vozilo.getGpsSirina());
			s.setDouble(17, vozilo.getGpsDuzina());

			return s.executeUpdate() == 1 ? vozilo : null;

		} catch (SQLException ex) {
			Logger.getLogger(VozilaDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Kreira i vraća objekt {@code Vozila} iz rezultata SQL upita.
	 *
	 * @param rs rezultat SQL upita
	 * @return objekt {@code Vozila} s podacima iz rezultata upita
	 * @throws SQLException ako dođe do greške prilikom čitanja podataka iz
	 *                      rezultata upita
	 */
	private Vozila kreirajObjektVozila(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int broj = rs.getInt("broj");
		long vrijeme = rs.getLong("vrijeme");
		double brzina = rs.getDouble("brzina");
		double snaga = rs.getDouble("snaga");
		double struja = rs.getDouble("struja");
		double visina = rs.getDouble("visina");
		double gpsBrzina = rs.getDouble("gpsBrzina");
		int tempVozila = rs.getInt("tempVozila");
		int postotakBaterija = rs.getInt("postotakBaterija");
		double naponBaterija = rs.getDouble("naponBaterija");
		int kapacitetBaterija = rs.getInt("kapacitetBaterija");
		int tempBaterija = rs.getInt("tempBaterija");
		double preostaloKm = rs.getDouble("preostaloKm");
		double ukupnoKm = rs.getDouble("ukupnoKm");
		double gpsSirina = rs.getDouble("gpsSirina");
		double gpsDuzina = rs.getDouble("gpsDuzina");

		return new Vozila(id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, tempVozila,
				postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm,
				ukupnoKm, gpsSirina, gpsDuzina);
	}
}
