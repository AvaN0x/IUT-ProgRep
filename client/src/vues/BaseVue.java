package client.src.vues;

import java.io.IOException;
import java.net.URL;

import client.src.controleurs.BaseControleur;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class BaseVue extends Stage {
    protected BaseControleur _controleur;

    public BaseVue(String nomFichier) throws IOException {
        URL fxmlURL = getClass().getResource(nomFichier);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
        var node = (ScrollPane) fxmlLoader.load();

        _controleur = (BaseControleur) fxmlLoader.getController();
        _controleur.setVue(this);

        this.setScene(new Scene(node));
        this.getIcons().add(new Image("file:client/res/icon.png"));
        this.initModality(Modality.APPLICATION_MODAL);
    }
}
