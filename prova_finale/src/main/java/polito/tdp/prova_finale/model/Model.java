package polito.tdp.prova_finale.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

	List<List<Player>> listaListe;

	boolean trovata;
	List<TeamPlayer> soluzione;

	public Model() {
		dao = new PlayersDAO();

	}

	public List<TeamPlayer> creaGrafo(String formation, Integer intesa, Integer maxLeagues, String quality,
			Integer overall) {

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

		listaListe = new ArrayList<>();
		// Preparo una mappa contenente i giocatori che posso mettere
		// in ogni ruolo in base ai vincoli dell'utente
		mappaRuoli = new TreeMap<String, List<Player>>();

		for (TeamPlayer p : vertici) {

			String nomeRuolo = p.getRuolo().getName();

			// se non ho già preso i giocatori (esempio CB, che compare almeno due volte)
			if (!mappaRuoli.containsKey(nomeRuolo)) {

				List<Player> giocatoriRuolo = new ArrayList<>();

				giocatoriRuolo = this.dao.getPlayersByParameters(overall, quality, nomeRuolo);

				// PROBLEMI GIOCATORI NON TROVATI
				if (giocatoriRuolo.size() == 0) {
					giocatoriRuolo = this.dao.getPlayersByParameters(overall + 1, quality, nomeRuolo);
				}
				if (giocatoriRuolo.size() == 0) {
					System.out.println("Non è stato possibile trovare giocatori che rispettino i requisiti nel ruolo: "
							+ nomeRuolo);
					return null;
				}

				// riempio la mappa che segna le corrispondenze tra ruolo ed elenco giocatori
				// sarà questo l'elenco di giocatori su cui farò la ricorsione
				mappaRuoli.put(nomeRuolo, giocatoriRuolo);
			}

		}

		// Creo e inizializzo la soluzione parziale
		List<TeamPlayer> parziale = new ArrayList<>();

		// inizializzo inserendo un portiere:
		Player partenza = this.mappaRuoli.get("GK").get(0);

		for (TeamPlayer vertice : this.grafo.vertexSet()) {
			if (vertice.getRuolo().getName().equals("GK")) {
				vertice.setPlayer(partenza);
				parziale.add(vertice);
			}
		}

		trovata = false;

		ricorsione(parziale, intesa, maxLeagues);

		return soluzione;

	}

	/**
	 * Aggiungo in modo ricorsivo un giocatore dalla soluzione parziale a scelta tra
	 * quelli contenuti nella mappaRuoli (elenco di giocatori disponibili per ogni
	 * ruolo). Dopodiche faccio backtracking
	 * 
	 * @param parziale
	 * @param intesa
	 * @param maxLeagues
	 */
	private void ricorsione(List<TeamPlayer> parziale, Integer intesa, Integer maxLeagues) {

		// se ho trovato la soluzione risalgo i livelli della ricorsione
		if (trovata) {
			return;
		}

		// CASO TERMINALE
		// se ho 11 giocatori
		if (parziale.size() == 11) {

			// controllo il numero di campionati
			if (numeroCampionati(parziale) >= maxLeagues) {
				// controllo sull'intesa della squadra
				if (getIntesaSquadra() >= intesa) {
					this.soluzione = new ArrayList<>(parziale);
					trovata = true;
				}

			}

			return;
		}

		// Creazione di soluzioni
		for (TeamPlayer vicino : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size() - 1))) {

			// se non ho ancora definito il giocatore nel grafo
			if (vicino.getPlayer() == null) {

				// scorro tra i giocatori possibili
				for (Player p : mappaRuoli.get(vicino.getRuolo().getName())) {

					// se non ho già ingaggiato il giocatore nella squadra parziale
					if (!parziale.contains(p)) {
						// riempio il vertice nullo con il giocatore nel grafo
						vicino.setPlayer(p);
						// aggiungo il giocatore alla lista dei giocatori
						parziale.add(vicino);
						// aggiungo gli archi
						for (TeamPlayer vicino2 : Graphs.neighborListOf(this.grafo, vicino)) {
							if (vicino2.getPlayer() != null) {
								this.grafo.setEdgeWeight(this.grafo.getEdge(vicino, vicino2),
										getPeso(vicino.getPlayer(), vicino2.getPlayer()));
							}
						}
						// faccio ricorsione
						ricorsione(parziale, intesa, maxLeagues);
						// faccio backtracking sulla lista e sul grafo (rimuovendo il vertice e
						// rimettendo i pesi precedenti
						parziale.remove(vicino);
						// riporto i pesi alla situazione iniziale
						for (TeamPlayer vicino2 : Graphs.neighborListOf(this.grafo, vicino)) {
							if (vicino2.getPlayer() != null) {
								this.grafo.setEdgeWeight(this.grafo.getEdge(vicino, vicino2), -2);
							}
						}
						vicino.setPlayer(null);

					}
				}
			}

		}

	}

	public Integer getIntesaSquadra() {

		Integer intesa = 0;

		for (TeamPlayer tp : this.grafo.vertexSet()) {

			intesa += getIntesaGiocatore(tp);
		}

		return intesa;
	}

	public Integer getIntesaGiocatore(TeamPlayer tp) {

		Integer pesoTot = 0;

		for (TeamPlayer vicino : Graphs.neighborListOf(this.grafo, tp)) {

			Integer pesoArco = (int) this.grafo.getEdgeWeight(this.grafo.getEdge(tp, vicino));
			pesoTot += pesoArco;
		}

		if (pesoTot >= 0) {
			return 10;
		} else if (pesoTot == -1) {
			return 7;
		} else {
			return 4;
		}

	}

	public Integer getPeso(Player p1, Player p2) {

		if (p1.getClub().equals(p2.getClub())
				|| (p1.getLeague().equals(p2.getLeague()) && p1.getNationality().equals(p2.getNationality()))) {
			return 1;
			// se sono dello stesso campionato o della stessa nazionalità, peso 0
		} else if (p1.getLeague().equals(p2.getLeague()) || p1.getNationality().equals(p2.getNationality())) {
			return 0;
			// se non hanno niente in comune, peso -1
		} else {
			return -1;
		}

	}

	public void setPesi() {

		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {

			Player p1 = this.grafo.getEdgeSource(e).getPlayer();
			Player p2 = this.grafo.getEdgeTarget(e).getPlayer();

			// Ora vedo cosa hanno in comune
			// se sono dello stesso club, oppure dello stesso campionato e stessa
			// nazionalità, peso 1
			if (p1.getClub().equals(p2.getClub())
					|| (p1.getLeague().equals(p2.getLeague()) && p1.getNationality().equals(p2.getNationality()))) {
				this.grafo.setEdgeWeight(e, 1);
				// se sono dello stesso campionato o della stessa nazionalità, peso 0
			} else if (p1.getLeague().equals(p2.getLeague()) || p1.getNationality().equals(p2.getNationality())) {
				this.grafo.setEdgeWeight(e, 0);
				// se non hanno niente in comune, peso 0
			} else {
				this.grafo.setEdgeWeight(e, 0);
			}
		}

	}

	/**
	 * Conta il numero di campionati diversi presente in una lista di giocatori
	 * 
	 * @param parziale
	 * @return
	 */
	public int numeroCampionati(List<TeamPlayer> parziale) {

		Set<String> campionati = new HashSet<>();

		for (TeamPlayer p : parziale) {

			campionati.add(p.getPlayer().getLeague());
		}

		return campionati.size();
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
