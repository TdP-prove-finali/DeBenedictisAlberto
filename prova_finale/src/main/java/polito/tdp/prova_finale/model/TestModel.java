package polito.tdp.prova_finale.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {

		Model model = new Model();

		List<TeamPlayer> soluzione = model.creaGrafo("4-3-3", 10, 3, "Gold - Non-Rare", 76);
		
		System.out.println(soluzione);
		
	}

}
