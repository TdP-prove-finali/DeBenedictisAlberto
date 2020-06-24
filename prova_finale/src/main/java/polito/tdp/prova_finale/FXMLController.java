/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package polito.tdp.prova_finale;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="choiceQuality"
    private ChoiceBox<?> choiceQuality; // Value injected by FXMLLoader

    @FXML // fx:id="txtIntesa"
    private TextField txtIntesa; // Value injected by FXMLLoader

    @FXML // fx:id="btnTrova"
    private Button btnTrova; // Value injected by FXMLLoader

    @FXML // fx:id="btnCosto"
    private Button btnCosto; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doRiduciCosto(ActionEvent event) {

    }

    @FXML
    void doTrova(ActionEvent event) {

    }

    @FXML
    void txtCampionati(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert choiceQuality != null : "fx:id=\"choiceQuality\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtIntesa != null : "fx:id=\"txtIntesa\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnTrova != null : "fx:id=\"btnTrova\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCosto != null : "fx:id=\"btnCosto\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
}
