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

	// VARIABILI PER REQUISITI

	Integer min_GiocatoriPerRuolo;
	Integer intesa_richiesta;
	Integer overall_richiesto;
	Integer num_camp_richiesto;

	// VARIABILI PER LA SOLUZIONE

	boolean trovata;
	List<TeamPlayer> soluzione;
	List<Player> squadraSol;
	int intesaSol;
	int numCampionatiSol;
	int overallSol;
	int prezzoSol;

	int trovate = 0;

	// VARIABILI PER COSTO RIDOTTO
	int sostituzioni_eff;
	List<TeamPlayer> ecoSol;
	List<Player> ecoSquadra;
	Integer ecoIntesa;
	Integer ecoOverall;
	Integer ecoPrezzo;

	public Model() {
		dao = new PlayersDAO();

	}

	public List<TeamPlayer> creaGrafo(String formation, Integer intesa, Integer minLeagues, String quality,
			Integer overall) {

		if (formation.equals("4-4-2")) {
			this.min_GiocatoriPerRuolo = 2;
		} else {
			this.min_GiocatoriPerRuolo = 3;
		}

		// prendo alcuni dei parametri
		this.intesa_richiesta = intesa;
		this.overall_richiesto = overall;
		this.num_camp_richiesto = minLeagues;

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

		// -----------------------------------

		System.out.println("Ottengo i giocatori per ogni ruolo: \n");

		for (TeamPlayer p : vertici) {

			String nomeRuolo = p.getRuolo().getName();

			// se non ho già preso i giocatori (esempio CB, che compare almeno due volte)
			if (!mappaRuoli.containsKey(nomeRuolo)) {

				List<Player> giocatoriRuolo = new ArrayList<>();

				giocatoriRuolo = this.dao.getPlayersByParameters(overall, quality, nomeRuolo);

				// PROBLEMI GIOCATORI NON TROVATI

				// cerco giocatori con overall crescente fin quando non ne trovo almeno 3
				// (uso valori di overall e qualità "incrementati" per agevolare la ricerca)
				int incr_overall = overall;
				String incr_quality = quality;
				while (giocatoriRuolo.size() < this.min_GiocatoriPerRuolo && incr_overall <= 93) {

					// Accordiamo qualità e overall per trovare i risultati
					if (incr_overall == 64) {
						incr_quality = quality.replace("Bronze", "Silver");
					} else if (incr_overall == 74) {
						incr_quality = quality.replace("Silver", "Gold");
					} else if (incr_overall > 83) {
						incr_quality = quality.replace("Non-Rare", "Rare");
					}

					List<Player> giocatoriDaAggiungere = new ArrayList<>();

					giocatoriDaAggiungere = this.dao.getPlayersByParameters(incr_overall, incr_quality, nomeRuolo);
					incr_overall++;

					if (giocatoriDaAggiungere.size() > 0) {
						for (Player daAgg : giocatoriDaAggiungere) {
							giocatoriRuolo.add(daAgg);
						}
					}
				}

				// fin quando non ho almeno 3 giocatori per ruolo, cerco quelli con overall
				// maggiore
				if (giocatoriRuolo.size() < this.min_GiocatoriPerRuolo) {
					System.out.println("Non è stato possibile trovare abbastanza giocatori nel ruolo " + nomeRuolo);
					return null;
				}

				// riempio la mappa che segna le corrispondenze tra ruolo ed elenco giocatori
				// sarà questo l'elenco di giocatori su cui farò la ricorsione
				mappaRuoli.put(nomeRuolo, giocatoriRuolo);
				System.out.println("Numero giocatori per il ruolo " + nomeRuolo + ": " + giocatoriRuolo.size());
			}

		}

		// ----------------------------------
		// fine della preparazione
		// ----------------------------------

		// Creo e inizializzo la soluzione parziale
		List<TeamPlayer> parziale = new ArrayList<>();
		List<Player> squadra = new ArrayList<>();

		this.trovata = false;
		this.intesaSol = 0;
		this.numCampionatiSol = 0;
		this.overallSol = 0;

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
			// DEBUG
			// trovate++;
			// System.out.println(trovate);

			// CONTROLLO NUM CAMPIONATI
			if (numeroCampionati(parziale) >= minLeagues) {

				// carico giocatori e pesi nel grafo
				caricaGiocatori(parziale);
				caricaPesi();

				// CONTROLLO OVERALL
				if (overallSquadra(squadra) >= overall) {

					// CONTROLLO INTESA
					if (getIntesaSquadra() >= intesa) {

						this.numCampionatiSol = numeroCampionati(parziale);
						this.overallSol = overallSquadra(squadra);
						this.intesaSol = getIntesaSquadra();
						this.soluzione = new ArrayList<>(parziale);
						this.squadraSol = new ArrayList<>(squadra);
						this.prezzoSol = calcolaPrezzo(parziale);
						trovata = true;

						System.out.println("Controllo campionati superato: " + numeroCampionati(parziale) + "");
						System.out.println("Controllo overall superato: " + overallSquadra(squadra));
						System.out.println("Controllo intesa della squadra superato: " + getIntesaSquadra());

					}
					// }
				}
				// a questo punto risetto tutti i player del grafo a null e i pesi a 10
				resetGiocatoriPesi();
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

	public Integer calcolaPrezzo(List<TeamPlayer> squadra) {

		Integer prezzoTot = 0;

		for (TeamPlayer tp : squadra) {
			prezzoTot += tp.getPlayer().getPrice();
		}

		return prezzoTot;
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

			int intesaGiocatore = getIntesaGiocatore(tp);
			// System.out.println("intesa " + tp + " ----> " + intesaGiocatore);

			intesa += intesaGiocatore;
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
		Integer grado = this.grafo.degreeOf(tp);

		for (TeamPlayer vicino : Graphs.neighborListOf(this.grafo, tp)) {

			Integer pesoArco = (int) this.grafo.getEdgeWeight(this.grafo.getEdge(tp, vicino));
			pesoTot += pesoArco;
		}

		// System.out.println("\n\nGiocatore " + tp + "\npeso Tot: " + pesoTot);

		if (pesoTot >= 0) {
			return 10;
		} else if (pesoTot == -1 || (pesoTot == -2 && grado > 2) || (pesoTot == -3 && grado > 4)) {
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

		if (p1.getClub().equals(p2.getClub()) && p1.getNationality().equals(p2.getNationality())) {
			return 2;
		} else if (p1.getClub().equals(p2.getClub())
				|| (p1.getLeague().equals(p2.getLeague()) && p1.getNationality().equals(p2.getNationality()))) {
			return 1;
		} else if (p1.getLeague().equals(p2.getLeague()) || p1.getNationality().equals(p2.getNationality())) {
			return 0;
			// se non hanno niente in comune, peso -1
		} else {
			return -1;
		}

	}

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

		// TODO: aggiungere correction factor

		Double somma = 0.0;

		for (Player p : parziale) {
			somma += p.getOverall();
		}

		Double media_parz = somma / 11;

		Integer res = Math.toIntExact(Math.round(media_parz));
		
		return res;
		
		
		/*
		// OPZIONE CORRECTION FACTOR
		// correction factor
		Double cf = 0.0;

		for (Player p : parziale) {
			if (p.getOverall() > media_parz) {
				cf += (p.getOverall() - media_parz);
			}
		}

		Integer totale = Math.toIntExact(Math.round((somma + cf) / 11));

		return totale;
		
		*/
	}

	/**
	 * Cerca, per ogni giocatore, una versione più economica (con lo stesso
	 * campionato o della stessa nazionalità), se ha successo, controlla che i
	 * vincoli siano rispettati anche con quel giocatore (aggiornando i vertici del
	 * grafo e i suoi pesi). Se non li soddisfa, ripristina il grafo soluzione
	 */
	public List<TeamPlayer> riduciCosto(List<TeamPlayer> soluzione) {

		this.sostituzioni_eff = 0;
		this.ecoIntesa = 0;
		this.ecoOverall = 0;
		this.ecoSol = new ArrayList<>();
		this.ecoSquadra = new ArrayList<>();

		// copiamo i giocatori in una lista da iterare, così posso modificare parziale
		List<TeamPlayer> sol_iniziale = new ArrayList<>(soluzione);

		List<TeamPlayer> prova;
		List<Player> provaSquadra = new ArrayList<>(this.squadraSol);

		// PER OGNI GIOCATORE DELLA SOLUZIONE INIZIALE
		for (TeamPlayer p : sol_iniziale) {

			// se non ho ancora trovato una soluzione più economica, la lista di giocatori
			// più recente è la soluzione iniziale, viceversa è quella "eco"

			if (this.ecoSol.size() == 0) {
				prova = new ArrayList<>(this.soluzione);
				provaSquadra = new ArrayList<>(this.squadraSol);
			} else {
				prova = new ArrayList<>(this.ecoSol);
				provaSquadra = new ArrayList<>(this.ecoSquadra);
			}

			Ruolo ruolo_corr = p.getRuolo();

			// cerco un'alternativa più economica del giocatore:

			// lista dei giocatori da provare
			// (giocatori simili al sostituendo, ma con overall minore)
			List<Player> sostituti = this.dao.getSostituto(overall_richiesto - 1, p.getPlayer().getQuality(),
					p.getRuolo().getName(), p.getPlayer().getPrice());

			// se ho trovato dei sostituti per il ruolo corrente
			if (sostituti.size() > 0) {

				// RIMUOVO IL GIOCATORE CORRENTE
				prova.remove(p);
				provaSquadra.remove(p.getPlayer());

				// PROVO A SOSTITUIRLO (SE HO SUCCESSO, SALVO LA LISTA DI GIOCATORI)
				for (Player sos : sostituti) {

					// se non è già presente
					if (!provaSquadra.contains(sos)) {

						TeamPlayer giocatoreInProva = new TeamPlayer(sos, ruolo_corr);

						// creo la lista di p e tp su cui fare gli stessi controlli che facciamo per le
						// soluzioni
						prova.add(giocatoreInProva);
						provaSquadra.add(sos);

						// CONTROLLO CAMPIONATI
						if (numeroCampionati(prova) >= this.num_camp_richiesto) {

							// CONTROLLO OVERALL
							if (overallSquadra(provaSquadra) >= this.overall_richiesto) {

								// carico giocatori e pesi nel grafo
								caricaGiocatori(prova);
								caricaPesi();

								// CONTROLLO INTESA
								if (getIntesaSquadra() >= this.intesa_richiesta) {

									// SALVO LA SOLUZIONE AGGIORNATA NEL MODEL IN UNA VARIABILE DIVERSA

									this.ecoSquadra = new ArrayList<>(provaSquadra);
									this.ecoSol = new ArrayList<>(prova);
									Collections.sort(this.ecoSol);
									this.ecoIntesa = getIntesaSquadra();
									this.ecoOverall = overallSquadra(provaSquadra);

									this.ecoPrezzo = calcolaPrezzo(prova);

									// ripulisco il grafo, che torna con giocatori vuoti e archi con pesi default
									resetGiocatoriPesi();

									// segno la sostituzione
									this.sostituzioni_eff++;

									prova.remove(giocatoreInProva);
									provaSquadra.remove(sos);

									break;

								}
							}
						}
						// fine prova giocatore (se la sostituzione ha avuto successo, la soluzione è
						// aggiornata)
						if (prova.size() == 11 && provaSquadra.size() == 11)
							prova.remove(giocatoreInProva);
						provaSquadra.remove(sos);

					}
				}
			}

		}

		return this.ecoSol;
	}

	/**
	 * Conta il numero di campionati diversi presente in una lista di giocatori
	 * 
	 * @param parziale
	 * @return
	 */

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

	public int getEcoIntesa() {
		return this.ecoIntesa;
	}

	public int getPrezzoSol() {
		return this.prezzoSol;
	}

	public int getEcoPrezzo() {
		return this.ecoPrezzo;
	}

	public int getEcoOverall() {
		return this.ecoOverall;
	}

}
