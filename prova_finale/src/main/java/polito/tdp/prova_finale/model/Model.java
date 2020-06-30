package polito.tdp.prova_finale.model;

import polito.tdp.prova_finale.db.PlayersDAO;

public class Model {

	PlayersDAO dao;

	public Model() {
		dao = new PlayersDAO();
	}

}
