package polito.tdp.prova_finale.model;

public class TeamPlayer {

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

}
