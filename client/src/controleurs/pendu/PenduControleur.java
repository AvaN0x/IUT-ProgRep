package client.src.controleurs.pendu;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.UUID;

import client.src.ClientMain;
import commun.IPendu;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PenduControleur extends client.src.controleurs.BaseControleur {
    private IPendu partie;
    private UUID id;

    @FXML
    private Group grp_pendu;
    @FXML
    private Button btn_jouer;
    @FXML
    private Group grp_mot;
    @FXML
    private TextField tf_lettre;

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
        btn_jouer.setVisible(false);
        this.id = partie.nouveauSalon();

        var indices = partie.recupIndice(this.id);
        // En créant une variable ici, on évite de faire un requête au serveur pour
        // chaque itération
        int nbLettres = partie.recupNbLettres(this.id);
        for (int i = 0; i < nbLettres; i++) {
            Label lbl_placeholder = new Label("_", grp_mot);
            // Si un indice est à cette position
            if (indices.get(i) != null) {
                lbl_placeholder.setText(Character.toString(indices.get(i)));
            }
        }
    }

    private void handleLettre() throws RemoteException {
        var res = partie.envoiLettre(this.id, tf_lettre.getText().charAt(0));
        afficherPendu(res.getVie());
        if (res.getVie() == 0) {
            finir("Vous avez perdu...");
        } else if (res.getPositionLettre() != -1) {
            boolean fini = true;
            for (int i = 0; i < grp_mot.getChildren().size(); i++) {
                Label lettre = (Label) grp_mot.getChildren().get(i);

                if (res.getPositionLettre() == i) {
                    lettre.setText(tf_lettre.getText());
                }

                if (lettre.getText().equals("_")) {
                    fini = false;
                }
            }

            if (fini) {
                finir("Vous avez gagné !");
            }
        }
    }

    public void finir(String texte) {
        // grp_mot.getChildren().clear();
        new Label(texte, grp_mot);
        btn_jouer.setVisible(true);
        quitter();
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
