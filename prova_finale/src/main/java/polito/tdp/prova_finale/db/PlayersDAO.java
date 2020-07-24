package polito.tdp.prova_finale.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import polito.tdp.prova_finale.model.CoppiaRuoli;
import polito.tdp.prova_finale.model.Player;
import polito.tdp.prova_finale.model.Ruolo;

public class PlayersDAO {

	/**
	 * Data la qualità dei giocatori, l'overall e il ruolo, determina una lista di
	 * giocatori che soddisfa i parametri (proveniente da uno dei 5 campionati
	 * principali)
	 * 
	 * @param overall valutazione giocatore
	 * @param quality qualità dei giocatori
	 * @return
	 */
	public List<Player> getPlayersByParameters(Integer overall, String quality, String ruolo) {

		String sql = "select futbin_id as id, overall, position, price, player_name as name, player_extended_name as ext_name, quality,  club, league, nationality "
				+ "from players " + "where quality = ? " + "AND overall = ? " + "AND origin = \"\" "
				+ "AND position = ? " + "AND revision = \"Normal\" " + "Order by price ASC";

		List<Player> result = new ArrayList<Player>();

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			// metto i parametri
			st.setString(1, quality);
			st.setInt(2, overall);
			st.setString(3, ruolo);

			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player p = new Player(res.getInt("id"), res.getInt("overall"), res.getString("position"),
						res.getInt("price"), res.getString("name"), res.getString("ext_name"), res.getString("quality"),
						res.getString("club"), res.getString("league"), res.getString("nationality"));

				result.add(p);
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public List<CoppiaRuoli> getCoppie(String formation) {

		String sql = "select id1,id2 " + "from links " + "where formation = ?";

		List<CoppiaRuoli> result = new ArrayList<>();

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			st.setString(1, formation);

			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(new CoppiaRuoli(res.getInt("id1"), res.getInt("id2")));

			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public List<Ruolo> getRuoli() {

		String sql = "select * from roles";

		List<Ruolo> result = new ArrayList<>();

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(new Ruolo(res.getInt("role_id"), res.getString("role_name"),
						res.getString("role_description")));

			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public List<String> getQuality() {

		String sql = "select distinct quality " + "from players";

		List<String> result = new ArrayList<>();

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(res.getString("quality"));

			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Ritorna le formazioni da caricare nel menù a tendina
	 * 
	 * @return
	 */
	public List<String> getFormations() {

		String sql = "select distinct formation " + "from links";

		List<String> result = new ArrayList<>();

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(res.getString("formation"));

			}
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

}
