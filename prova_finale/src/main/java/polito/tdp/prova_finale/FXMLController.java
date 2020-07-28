/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package polito.tdp.prova_finale;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import polito.tdp.prova_finale.model.Model;
import polito.tdp.prova_finale.model.Player;
import polito.tdp.prova_finale.model.TeamPlayer;

public class FXMLController {

	Model model;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="choiceQuality"
	private ChoiceBox<String> choiceQuality; // Value injected by FXMLLoader

	@FXML // fx:id="btnTrova"
	private Button btnTrova; // Value injected by FXMLLoader

	@FXML // fx:id="btnCosto"
	private Button btnCosto; // Value injected by FXMLLoader

	@FXML // fx:id="choiceModulo"
	private ChoiceBox<String> choiceModulo; // Value injected by FXMLLoader

	@FXML // fx:id="choiceLeagues"
	private ChoiceBox<Integer> choiceLeagues; // Value injected by FXMLLoader

	@FXML // fx:id="txtOverall"
	private TextField txtOverall; // Value injected by FXMLLoader

	@FXML // fx:id="choiceIntesa"
	private ChoiceBox<Integer> choiceIntesa; // Value injected by FXMLLoader

	@FXML // fx:id="txtResult"
	private TextArea txtResult; // Value injected by FXMLLoader

	@FXML
	void doRiduciCosto(ActionEvent event) {

		txtResult.setStyle("-fx-font-family: monospace");

		if (choiceQuality.getValue() == null || choiceModulo.getValue() == null || choiceIntesa.getValue() == null
				|| choiceLeagues.getValue() == null || txtOverall.getText().isEmpty()) {
			txtResult.setText("\nERRORE: selezionare tutti i parametri");
			return;
		} else {

			String quality = choiceQuality.getValue();
			String modulo = choiceModulo.getValue();
			Integer intesa = choiceIntesa.getValue();
			Integer minLeagues = choiceLeagues.getValue();

			// ABBIAMO VERIFICATO LA PRESENZA DI TUTTI I PARAMETRI, ORA VERIFICHIAMO CHE
			// L'OVERALL SIA UN NUMERO
			String overall_str = txtOverall.getText();
			Integer overall = 0;

			try {
				overall = Integer.parseInt(overall_str);
			} catch (NumberFormatException e) {
				txtResult.setText("Inserire un numero intero compreso tra 55 e 84");
				return;
			}

			// Una volta arrivati qui, sappiamo che è stato inserito un intero, ma ora
			// bisogna verificarne
			// la compatibilità con la qualità dei giocatori
			if (overall < 55 || overall > 84) {
				txtResult.setText(
						"Hai inserito un overall non valido.\nIn base alla qualità dei giocatori scelti, puoi indicare un overall compreso tra 55 e 84.");
				return;
			} else {

				List<TeamPlayer> soluzione = model.getSoluzione();

				if (soluzione == null) {
					txtResult.appendText("Prima di ridurre il costo devi trovare una squadra");
					return;
				} else {

					List<TeamPlayer> ecoSol = model.riduciCosto(soluzione, overall, quality, minLeagues, intesa);

					if (ecoSol == null) {
						txtResult.setText("Non è stato possibile ridurre il costo della soluzione corrente");
						return;
					} else {
						txtResult.setText("E' stata trovata una soluzione più economica, ");

						int sostituzioni = model.getSostituzioni();

						if (sostituzioni == 1) {
							txtResult.appendText("effettuando la seguente sostituzione:\n");
						} else {
							txtResult.appendText("effettuando le seguente sostituzioni:\n");
						}

						int risparmioTot = 0;

						// dichiaro le differenze
						for (int i = 0; i < 11; i++) {

							Player p_vecchio = soluzione.get(i).getPlayer();
							Player p_nuovo = ecoSol.get(i).getPlayer();

							int risparmio = p_vecchio.getPrice() - p_nuovo.getPrice();

							risparmioTot += risparmio;

							if (!p_vecchio.equals(p_nuovo)) {
								txtResult.appendText("- " + p_nuovo.getName() + " al posto di " + p_vecchio.getName()
										+ " nel ruolo " + p_vecchio.getPosition() + ", risparmiando " + risparmio
										+ " crediti\n");
							}
						}
						txtResult.appendText("\n");

						txtResult.appendText("Totale: " + model.getEcoOverall() + "\n");
						txtResult.appendText("Intesa: " + model.getEcoIntesa() + "\n");
						txtResult.appendText("Risparmio totale: " + risparmioTot + " crediti\n\n");

						for (TeamPlayer tp : ecoSol) {
							txtResult.appendText(String.format("%-5s %-30s %d %-20s %d\n", tp.getRuolo().getName(),
									tp.getRuolo().getDescription(), tp.getPlayer().getOverall(),
									tp.getPlayer().getName(), tp.getPlayer().getPrice()));

						}

						txtResult.appendText("\nPrezzo: " + model.getEcoPrezzo() + " crediti\n");

					}

				}

			}
		}

	}

	@FXML
	void doTrova(ActionEvent event) {

		txtResult.clear();

		txtResult.setStyle("-fx-font-family: monospace");

		if (choiceQuality.getValue() == null || choiceModulo.getValue() == null || choiceIntesa.getValue() == null
				|| choiceLeagues.getValue() == null || txtOverall.getText().isEmpty()) {
			txtResult.setText("\nERRORE: selezionare tutti i parametri");
			return;
		} else {

			String quality = choiceQuality.getValue();
			String modulo = choiceModulo.getValue();
			Integer intesa = choiceIntesa.getValue();
			Integer minLeagues = choiceLeagues.getValue();

			// ABBIAMO VERIFICATO LA PRESENZA DI TUTTI I PARAMETRI, ORA VERIFICHIAMO CHE
			// L'OVERALL SIA UN NUMERO
			String overall_str = txtOverall.getText();
			Integer overall = 0;

			try {
				overall = Integer.parseInt(overall_str);
			} catch (NumberFormatException e) {
				txtResult.setText("Inserire un numero intero compreso tra 55 e 84");
				return;
			}

			// Una volta arrivati qui, sappiamo che è stato inserito un intero, ma ora
			// bisogna verificarne
			// la compatibilità con la qualità dei giocatori
			if (overall < 55 || overall > 84) {
				txtResult.setText(
						"Hai inserito un overall non valido.\nIn base alla qualità dei giocatori scelti, puoi indicare un overall compreso tra 55 e 84.");
				return;
			} else {

				// E' stato inserito un intero compreso tra 55 e 84, ora vediamo se è
				// stata inserita la qualità corrispondente

				if ((overall >= 55 && overall <= 64 && !quality.contains("Bronze"))) {
					txtResult.setText(
							"Con un overall compreso tra 55 e 64, è possibile formare una squadra con giocatori di qualità BRONZE.");
					return;
				} else if (overall >= 65 && overall <= 74 && !quality.contains("Silver")) {
					txtResult.setText(
							"Con un overall compreso tra 65 e 74, è possibile formare una squadra con giocatori di qualità SILVER.");
					return;
				} else if (overall >= 75 && !quality.contains("Gold")) {
					txtResult.setText(
							"Con un overall maggiore di 75, è possibile formare una squadra con giocatori di qualità GOLD.");
					return;
				} else {

					// la qualità è corretta, ora possiamo provare ad eseguire la funzione
					List<TeamPlayer> soluzione = model.creaGrafo(modulo, intesa, minLeagues, quality, overall);

					if (soluzione != null) {

						txtResult.setText("SQUADRA TROVATA!\n");

						txtResult.appendText("Totale: " + model.getOverallSol() + "\n");
						txtResult.appendText("Intesa: " + model.getIntesaSol() + "\n\n");

						for (TeamPlayer tp : soluzione) {
							txtResult.appendText(String.format("%-5s %-30s %d %-20s %d\n", tp.getRuolo().getName(),
									tp.getRuolo().getDescription(), tp.getPlayer().getOverall(),
									tp.getPlayer().getName(), tp.getPlayer().getPrice()));

						}
						txtResult.appendText("\nPrezzo: " + model.getPrezzoSol() + " crediti\n");
					} else {
						System.out.println(
								"Hai imposto dei vincoli troppo restrittivi, non è stato possibile trovare una squadra.");
					}

				}

			}

		}

	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert choiceQuality != null : "fx:id=\"choiceQuality\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnTrova != null : "fx:id=\"btnTrova\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnCosto != null : "fx:id=\"btnCosto\" was not injected: check your FXML file 'Scene.fxml'.";
		assert choiceModulo != null : "fx:id=\"choiceModulo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert choiceLeagues != null : "fx:id=\"choiceLeagues\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtOverall != null : "fx:id=\"txtOverall\" was not injected: check your FXML file 'Scene.fxml'.";
		assert choiceIntesa != null : "fx:id=\"choiceIntesa\" was not injected: check your FXML file 'Scene.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

	}

	public void setModel(Model model) {

		this.model = model;

		// Carico gli elementi nei vari menù a tendina:

		// formazioni
		choiceModulo.getItems().addAll(this.model.getFormations());
		// numero campionati
		for (int i = 1; i <= 5; i++) {
			choiceLeagues.getItems().add(i);
		}

		for (int i = 10; i <= 17; i++) {
			choiceIntesa.getItems().add(5 * i);
		}

		// qualità dei giocatori
		choiceQuality.getItems().addAll(this.model.getQuality());
	}

}
