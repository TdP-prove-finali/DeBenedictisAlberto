package polito.tdp.prova_finale.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import polito.tdp.prova_finale.model.Player;
import polito.tdp.prova_finale.model.Ruolo;

public class PlayersDAO {

	/**
	 * Data la qualità dei giocatori e il totale richiesto, ritorna una lista di
	 * giocatori presente nel range di valutazione richiesto (+ o - 1)
	 * 
	 * @param overall valutazione giocatore
	 * @param quality qualità dei giocatori
	 * @return
	 */
	public List<Player> getPlayersByOverallQuality(Integer overall, String quality) {

		return null;
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
