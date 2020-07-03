package polito.tdp.prova_finale.db;

public class TestDAO {

	public static void main(String[] args) {

		PlayersDAO dao = new PlayersDAO();

		// prova per caricare le formazioni
		System.out.println(dao.getFormations());
		System.out.println(dao.getQuality());
		System.out.println(dao.getRuoli());
	}

}
