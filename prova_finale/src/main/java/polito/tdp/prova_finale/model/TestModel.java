package polito.tdp.prova_finale.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {

		Model model = new Model();

		List<TeamPlayer> soluzione = model.creaGrafo("4-3-3", 50, 1, "Bronze - Non-Rare", 60);
		
		System.out.println("\n\n\n");
		
		if(soluzione!= null) {
		
		for(TeamPlayer tp: soluzione) {
			System.out.println(tp);
		}
		
		}else {
			System.out.println("Hai imposto dei vincoli troppo restrittivi, non è stato possibile trovare una squadra.");
		}
		
	}

}
