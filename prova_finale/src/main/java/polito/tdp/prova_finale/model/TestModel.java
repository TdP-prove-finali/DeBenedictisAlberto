package polito.tdp.prova_finale.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {

		Model model = new Model();

		/*
		 * 
		 * List<TeamPlayer> soluzione = model.creaGrafo("3-4-3", 60, 1, "Bronze - Rare",
		 * 52);
		 * 
		 * System.out.println("\n\n\n");
		 * 
		 * if (soluzione!= null) {
		 * 
		 * for (TeamPlayer tp : soluzione) { System.out.println(tp); }
		 * 
		 * System.out.println("\nPrezzo: " + model.getPrezzoSol() + "\n");
		 * 
		 * } else { System.out
		 * .println("Hai imposto dei vincoli troppo restrittivi, non è stato possibile trovare una squadra."
		 * ); }
		 * 
		 * if (soluzione != null) {
		 * 
		 * System.out.println("\nProvo a ridurre il costo: ");
		 * 
		 * // PROVA RIDUCI COSTO
		 * 
		 * List<TeamPlayer> soluzione_economica = model.riduciCosto(soluzione);
		 * 
		 * if (soluzione_economica.size() > 0) {
		 * 
		 * for (TeamPlayer tp : soluzione_economica) { System.out.println(tp); }
		 * 
		 * // stampo prezzo System.out.println("\nTotale: " + model.getEcoOverall() +
		 * "\nIntesa: " + model.getEcoIntesa() + "\nPrezzo: " + model.getEcoPrezzo());
		 * 
		 * } else { System.out.
		 * println("Non è stato possibile trovare un'alternativa più economica"); }
		 * 
		 * }
		 * 
		 * // PROVA REPLACE /* String prova = "prova - Alberto"; prova =
		 * prova.replace("prova", "oro"); System.out.println(prova);
		 */

		// PROVA FORMATTAZIONE

		String a = "ciao";
		String b = "cccccccccccccccccccccccc";
		String c = "elena";
		int overall = 81;

		System.out.println(String.format("%-30s %d %s", a,overall, c));
		System.out.println(String.format("%-30s %d %s", b,overall, c));

	}

}
