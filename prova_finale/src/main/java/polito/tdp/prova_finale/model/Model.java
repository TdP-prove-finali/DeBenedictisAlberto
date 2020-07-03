package polito.tdp.prova_finale.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import polito.tdp.prova_finale.db.PlayersDAO;

public class Model {

	PlayersDAO dao;
	Graph<TeamPlayer, DefaultWeightedEdge> grafo;
	Map<Integer, Ruolo> ruoliIdMap;

	Map<String, List<Player>> mappaRuoli;
	
	
	
	public Model() {
		dao = new PlayersDAO();

	}

	public void creaGrafo(String formation, Integer intesa, Integer maxLeagues, String quality, Integer overall) {

		// carico la mappa dei ruoli
		ruoliIdMap = new HashMap<>();
		List<Ruolo> ruoli = this.dao.getRuoli();
		for (Ruolo r : ruoli) {
			ruoliIdMap.put(r.getId(), r);
		}

		// creo il grafo pesato con archi non diretti
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// In base alla formazione scelta dall'utente, creo un GRAFO DI RIFERIMENTO:
		// questo sarà formato da degli oggetti TeamPlayer con i ruoli definiti, ma i
		// player vuoti, ovvero messi a null

		// Uso la classe di supporto coppia ruoli per creare archi e vertici
		// contemporaneamente
		List<CoppiaRuoli> coppie = this.dao.getCoppie(formation);

		// scorro la lista delle coppie, controllando la presenza o meno dei vertici nel
		// grafo tramite l'idMap
		for (CoppiaRuoli c : coppie) {

			// prendo i ruoli della coppia
			Ruolo r1 = ruoliIdMap.get(c.getId1());
			Ruolo r2 = ruoliIdMap.get(c.getId2());

			TeamPlayer p1 = new TeamPlayer(null, r1);
			TeamPlayer p2 = new TeamPlayer(null, r2);

			// controllo se il grafo ha già questi vertici, altrimenti li aggiungo
			if (!this.grafo.containsVertex(p1)) {
				grafo.addVertex(p1);
			}
			if (!this.grafo.containsVertex(p2)) {
				grafo.addVertex(p2);
			}
			// inizialmente metto un peso fittizio, che aggiornerò a squadra terminata
			if (!this.grafo.containsEdge(p1, p2)) {
				Graphs.addEdge(this.grafo, p1, p2, 2);
			}

		}

		System.out.println(
				"Grafo creato!\n#Vertici " + this.grafo.vertexSet().size() + "\n#Archi " + this.grafo.edgeSet().size());

		// Preparo la lista per la ricorsione, la ordino per ruolo
		List<TeamPlayer> vertici = new ArrayList<>(this.grafo.vertexSet());
		Collections.sort(vertici);

		// Preparo una mappa contenente i giocatori che posso mettere
		// in ogni ruolo in base ai vincoli dell'utente
		mappaRuoli = new TreeMap<String, List<Player>>();

		for (TeamPlayer p : vertici) {

			String nomeRuolo = p.getRuolo().getName();

			
			if(!mappaRuoli.containsKey(nomeRuolo)){
			
			List<Player> giocatoriRuolo = new ArrayList<>();
			
			giocatoriRuolo = this.dao.getPlayersByParameters(overall, quality, nomeRuolo);

			if(giocatoriRuolo.size() == 0) {
				giocatoriRuolo = this.dao.getPlayersByParameters(overall+1, quality, nomeRuolo);
			}
			if(giocatoriRuolo.size() == 0) {
				System.out.println("Non è stato possibile trovare giocatori che rispettino i requisiti nel ruolo: "+nomeRuolo);
				return;
			}
			
			System.out.println("Lista giocatore per ruolo "+nomeRuolo+"  creata. Contenuto: " + giocatoriRuolo.size());

			for (Player gioc : giocatoriRuolo) {
				System.out.println(gioc + "\n");
			}

			mappaRuoli.put(nomeRuolo, giocatoriRuolo);
			}
		}

		for (List<Player> lista : mappaRuoli.values()) {

			for (Player p : lista) {
				System.out.println(p + "\n");
			}
		}

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
