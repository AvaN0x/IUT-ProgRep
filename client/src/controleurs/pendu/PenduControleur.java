package client.src.controleurs.pendu;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import client.src.controleurs.BaseControleur;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.UUID;

import client.src.ClientMain;
import commun.IPendu;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;

public class PenduControleur extends BaseControleur {
    private IPendu partie;
    private UUID id;

    @FXML
    private Group grp_pendu;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        try {
            this.partie = (IPendu) Naming.lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/pendu");

            Platform.runLater(() -> {
                afficherPendu(11);
                _vue.setOnCloseRequest((event) -> {
                    quitter();
                });
            });

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            showErreurAlerte("Pendu exception: ", e.toString());
            this.fermer();
        }
    }

    private void initPartie() throws RemoteException {
        this.id = partie.nouveauSalon();

        var indices = partie.recupIndice(this.id);
        int nbLettres = partie.recupNbLettres(this.id);
        for (int i = 0; i < nbLettres; i++) {
            // TODO: Dessiner ligne pour chaque lettre

            // Si un indice est à cette position
            if (indices.get(i) != null) {
                // TODO: afficher lettre
            }
        }
    }

    private void handleLettre() throws RemoteException {
        partie.envoiLettre(this.id, 'l'); // TODO: Lettre choisie par l'utilisateur
    }

    public void quitter() {
        try {
            if (id != null)
                this.partie.fermerSalon(this.id);
        } catch (RemoteException e) {
            showErreurAlerte("Pendu exception: ", e.toString());
        }
        this._vue.close();
    }

    private void afficherPendu(int stade) {
        Platform.runLater(() -> {
            for (int i = 0; i < grp_pendu.getChildren().size(); i++)
                grp_pendu.getChildren().get(i).setVisible(i < stade);
        });
    }

}
