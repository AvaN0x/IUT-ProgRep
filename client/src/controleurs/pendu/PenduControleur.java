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
    private Group grp_err;
    @FXML
    private TextField tf_lettre;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        try {
            this.partie = (IPendu) Naming.lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/pendu");

            Platform.runLater(() -> {
                afficherPendu(11 - IPendu.MAX_VIE);
                _vue.setOnCloseRequest((event) -> {
                    quitter();
                });
            });

            tf_lettre.textProperty().addListener((ov, oldValue, newValue) -> {
                if (tf_lettre.getText().length() > 1) {
                    String s = tf_lettre.getText().substring(0, 1);
                    tf_lettre.setText(s);
                }
            });

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            showErreurAlerte("Pendu exception: ", e.toString());
            this.fermer();
        }
    }

    public void initPartie() throws RemoteException {
        btn_jouer.setVisible(false);
        this.id = partie.nouveauSalon();

        var indices = partie.recupIndice(this.id);
        // En créant une variable ici, on évite de faire un requête au serveur pour
        // chaque itération
        grp_mot.getChildren().clear();
        grp_mot.setTranslateY(0);
        int nbLettres = partie.recupNbLettres(this.id);
        for (int i = 0; i < nbLettres; i++) {
            Label lbl_placeholder = new Label("_");
            lbl_placeholder.setTranslateX(8 * i);
            // Si un indice est à cette position
            if (indices.get(i) != null) {
                lbl_placeholder.setText(Character.toString(indices.get(i)));
            }
            grp_mot.getChildren().add(lbl_placeholder);
        }
    }

    public void handleLettre() throws RemoteException {
        var res = partie.envoiLettre(this.id, tf_lettre.getText().charAt(0));
        afficherPendu(11 - res.getVie());
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
        } else {
            Label lbl_err = new Label(tf_lettre.getText());
            lbl_err.setTranslateX(8 * grp_err.getChildren().size());
            grp_err.getChildren().add(lbl_err);
        }
    }

    public void finir(String texte) {
        grp_mot.getChildren().clear();
        Label lbl_fin = new Label(texte);
        grp_mot.getChildren().add(lbl_fin);
        grp_mot.setTranslateY(-24);
        btn_jouer.setVisible(true);
        try {
            if (id != null)
                this.partie.fermerSalon(this.id);
        } catch (RemoteException e) {
            showErreurAlerte("Pendu exception: ", e.toString());
        }
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
