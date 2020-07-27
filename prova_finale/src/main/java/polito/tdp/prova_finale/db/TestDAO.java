package polito.tdp.prova_finale.db;

import java.util.List;

import polito.tdp.prova_finale.model.Player;

public class TestDAO {

	public static void main(String[] args) {

		PlayersDAO dao = new PlayersDAO();

		// prova per caricare le formazioni
		System.out.println(dao.getFormations());
		System.out.println(dao.getQuality());
		System.out.println(dao.getRuoli());
		System.out.println(dao.getCoppie("4-3-3"));

		List<Player> lista = dao.getPlayersByParameters(78, "Gold - Non-Rare", "RM");

		for (Player p : lista) {
			System.out.println(p.toString() + "\n");
		}

	}

}
