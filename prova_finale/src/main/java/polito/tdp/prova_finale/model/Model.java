package polito.tdp.prova_finale.model;

import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import polito.tdp.prova_finale.db.PlayersDAO;

public class Model {

	PlayersDAO dao;
	Graph<TeamPlayer, DefaultWeightedEdge> grafo;
	Map<Integer, Ruolo> ruoliIdMap;

	public Model() {
		dao = new PlayersDAO();
		
	}

	public void creaGrafo(String formation, Integer intesa, Integer maxLeagues, String quality, Integer overall) {

		// creo il grafo pesato con archi non diretti
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		
		

	}

	/**
	 * Ritorna una lista delle formazioni possibili
	 * 
	 * @return
	 */
	public List<String> getFormations() {
		return this.dao.getFormations();
	}

	/**
	 * Ritorna la lista delle possibili qualità dei giocatori da utilizzare
	 * 
	 * @return
	 */
	public List<String> getQuality() {
		return this.dao.getQuality();
	}

}
