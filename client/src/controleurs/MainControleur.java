package client.src.controleurs;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainControleur extends BaseControleur {
    @FXML
    private Button btnPendu;
    @FXML
    private Button btnAllumettes;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        try {
        } catch (Exception e) {
            System.err.println(e.getClass().getSimpleName() + e.getMessage());
        }
    }

    public void openPendu() {
        // TODO
        System.out.println("openPendu");
    }

    public void openAllumettes() {
        try {
            new client.src.vues.allumettes.NewAllumettesVue().showAndWait();
        } catch (Exception e) {
            showErreurAlerte(e.getClass().getSimpleName(), e.getMessage());
        }
    }

}
