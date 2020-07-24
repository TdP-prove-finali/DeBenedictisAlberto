package polito.tdp.prova_finale.model;

public class TeamPlayer implements Comparable<TeamPlayer>{

	private Player player;
	private Ruolo ruolo;

	public TeamPlayer(Player player, Ruolo ruolo) {
		super();
		this.player = player;
		this.ruolo = ruolo;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Ruolo getRuolo() {
		return ruolo;
	}

	public void setRuolo(Ruolo ruolo) {
		this.ruolo = ruolo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ruolo == null) ? 0 : ruolo.hashCode());
		return result;
	}

	// Due teamPlayers sono uguali se hanno lo stesso id_ruolo: viene usato per controllare se ho già aggiunto il giocatore al grafo
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeamPlayer other = (TeamPlayer) obj;
		if (ruolo == null) {
			if (other.ruolo != null)
				return false;
		} else if (!ruolo.equals(other.ruolo))
			return false;
		return true;
	}

	// Vengono ordinati per id crescente
	@Override
	public int compareTo(TeamPlayer o) {
	
		return this.getRuolo().getId().compareTo(o.getRuolo().getId());
	}
	@Override
	public String toString() {
		return player +" "+ruolo;
	}
	
	
	
	
	

}
