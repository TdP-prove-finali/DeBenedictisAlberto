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

	List<TeamPlayer> vertici;

	boolean trovata;
	List<TeamPlayer> soluzione;

	public Model() {
		dao = new PlayersDAO();

	}

	public List<TeamPlayer> creaGrafo(String formation, Integer intesa, Integer minLeagues, String quality,
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

			// AGGIUNGO VERTICI E ARCHI

			// controllo se il grafo ha già questi vertici, altrimenti li aggiungo
			if (!this.grafo.containsVertex(p1)) {
				grafo.addVertex(p1);
			}
			if (!this.grafo.containsVertex(p2)) {
				grafo.addVertex(p2);
			}
			// inizialmente metto un peso fittizio, che aggiornerò a squadra terminata
			if (!this.grafo.containsEdge(p1, p2)) {
				Graphs.addEdge(this.grafo, p1, p2, 10);
			}

		}

		System.out.println(
				"Grafo creato!\n#Vertici " + this.grafo.vertexSet().size() + "\n#Archi " + this.grafo.edgeSet().size());

		// Preparo la lista per la ricorsione, la ordino per ruolo
		vertici = new ArrayList<>(this.grafo.vertexSet());
		Collections.sort(vertici);

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
				
				// cerco giocatori con overall crescente fin quando non ne trovo 1
				while (giocatoriRuolo.size() == 0 && overall <= 93) {
					giocatoriRuolo = this.dao.getPlayersByParameters(overall + 1, quality, nomeRuolo);
					overall++;
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

		// ---------------------------------- fine della preparazione
		// -----------------------------------------

		// Creo e inizializzo la soluzione parziale
		// provo a metterci i vertici piuttosto c
		List<TeamPlayer> parziale = new ArrayList<>();
		List<Player> squadra = new ArrayList<>();

		trovata = false;

		ricorsione(parziale, 0, squadra, intesa, minLeagues, overall);

		return soluzione;

	}

	/**
	 * Aggiungo in modo ricorsivo un giocatore dalla soluzione parziale a scelta tra
	 * quelli contenuti nella mappaRuoli (elenco di giocatori disponibili per ogni
	 * ruolo). Esploro quel ramo, faccio backtracking
	 * 
	 * @param parziale
	 * @param intesa
	 * @param maxLeagues
	 */
	private void ricorsione(List<TeamPlayer> parziale, int livello, List<Player> squadra, Integer intesa,
			Integer minLeagues, Integer overall) {

		// se ho trovato la soluzione risalgo i livelli della ricorsione
		if (trovata) {
			return;
		}

		// CASO TERMINALE
		// se ho 11 giocatori
		if (livello == 11) {
			
			// CONTROLLO NUM CAMPIONATI
			if (numeroCampionati(parziale) >= minLeagues) {

				// carico giocatori e pesi nel grafo
				caricaGiocatori(parziale);
				caricaPesi();

				// CONTROLLO OVERALL
				if (overallSquadra(squadra) >= overall) {

					// CONTROLLO INTESA
					if (getIntesaSquadra() >= intesa) {
						
						System.out.println("Controllo campionati superato: " + numeroCampionati(parziale) + "");
						System.out.println("Controllo overall superato: " + overallSquadra(squadra));
						System.out.println("Controllo intesa della squadra superato: " + getIntesaSquadra());

						this.soluzione = new ArrayList<>(parziale);
						trovata = true;
						// a questo punto risetto tutti i player del grafo a null e i pesi a 10
						resetGiocatoriPesi();
					}
					// }
				}
			}

			return;
		}

		// creazione di soluzioni parziali con lista:

		// avanzando di livello, da 0 a 10, prendo uno dei giocatori presenti nella
		// lista per
		// il ruolo attuale
		//
		for (Player p : this.mappaRuoli.get(vertici.get(livello).getRuolo().getName())) {

			// se il giocatore non è già in squadra
			if (!squadra.contains(p)) {

				// faccio ricorsione sul livello più basso
				TeamPlayer tp = new TeamPlayer(p, vertici.get(livello).getRuolo());
				parziale.add(tp);
				squadra.add(p);
				ricorsione(parziale, livello + 1, squadra, intesa, minLeagues, overall);

				// backtracking
				parziale.remove(tp);
				squadra.remove(p);

			}
		}

	}

	/**
	 * Carica la lista di giocatori come vertici del grafo scheletro
	 * 
	 * @param parziale
	 */
	public void caricaGiocatori(List<TeamPlayer> parziale) {

		// scorro tutti i giocatori: per ogni giocatore, scorro tutti gli archi e vedo
		// se l'id del ruolo del teamplayer (vertice)
		// è uguale all'id del ruolo del TeamPlayer

		for (TeamPlayer tp : parziale) {

			for (DefaultWeightedEdge e : this.grafo.edgeSet()) {

				if (this.grafo.getEdgeSource(e).getRuolo().equals(tp.getRuolo())) {

					this.grafo.getEdgeSource(e).setPlayer(tp.getPlayer());
				}

				if (this.grafo.getEdgeTarget(e).getRuolo().equals(tp.getRuolo())) {

					this.grafo.getEdgeTarget(e).setPlayer(tp.getPlayer());
				}

			}
		}
	}

	/**
	 * Mette i pesi corrispondenti in ogni arco, in base ai criteri dettati da
	 * getPeso()
	 */
	public void caricaPesi() {

		// scorro la lista degli archi, a ogni arco aggiungo il peso

		// System.out.println("Coppie di giocatori vicine:\n");

		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {

			// uno dei due giocatori è null per un certo arco
			Player p1 = this.grafo.getEdgeSource(e).getPlayer();
			Player p2 = this.grafo.getEdgeTarget(e).getPlayer();

			this.grafo.setEdgeWeight(e, getPeso(p1, p2));

		}

	}

	/**
	 * Ripristina la sitazione iniziale dei pesi, ovvero inserisco dei pesi fittizi
	 * in ogni arco (non essenziale dato che li sovrascrivo ogni volta) e metto i
	 * giocatori come NULL (il grafo ritorna ad essere solo lo scheletro)
	 */
	public void resetGiocatoriPesi() {

		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {
			;

			this.grafo.getEdgeSource(e).setPlayer(null);
			this.grafo.getEdgeTarget(e).setPlayer(null);
			this.grafo.setEdgeWeight(e, 10);

		}

	}

	/**
	 * Somma l'intesa di tutti i componenti della squadra
	 * 
	 * @return
	 */
	public Integer getIntesaSquadra() {

		Integer intesa = 0;

		for (TeamPlayer tp : this.grafo.vertexSet()) {

			intesa += getIntesaGiocatore(tp);
		}

		return intesa;
	}

	/**
	 * Funzione principale per il calcolo dell'intesa. In base alla somma dei pesi
	 * degli archi di ogni vertice, stabilisco un valore corrispondente (la
	 * conversione effettuata segue lo stesso criterio di quella utilizzata nel
	 * gioco).
	 * 
	 * @param tp
	 * @return
	 */
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

	/**
	 * Stabilisce il peso dell'arco tra due giocatori, in base al tipo e al numero
	 * di attributi che hanno in comune
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
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

	/**
	 * Cerca, per ogni giocatore, una versione più economica (con lo stesso
	 * campionato o della stessa nazionalità), se ha successo, controlla che i
	 * vincoli siano rispettati anche con quel giocatore (aggiornando i vertici del
	 * grafo e i suoi pesi). Se non li soddisfa, ripristina il grafo soluzione
	 */
	public void riduciCosto(List<TeamPlayer> parziale) {

		// scorro i giocatori della squadra creata
		for (TeamPlayer p : parziale) {

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
	 * Calcola l'overall della squadra, ottenuto come media aritmetica degli overall
	 * dei singoli giocatori
	 * 
	 * @param parziale
	 * @return
	 */
	public int overallSquadra(List<Player> parziale) {

		Integer somma = 0;

		for (Player p : parziale) {
			somma += p.getOverall();
		}

		Integer result = somma / 11;

		return result;
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
