package client.src.controleurs;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class BaseControleur implements javafx.fxml.Initializable {
    protected Stage _vue;

    public void setVue(Stage vue) {
        this._vue = vue;
    }

    protected void fermer() {
        this._vue.close();
    }

    protected void showAlerte(String titre, String entete, String contenu, Alert.AlertType type) {
        var alert = new Alert(type);
        alert.initOwner(_vue);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    protected void showErreurAlerte(String entete, String contenu) {
        showAlerte("Une erreur est survenue !", entete, contenu, Alert.AlertType.ERROR);
    }
}
