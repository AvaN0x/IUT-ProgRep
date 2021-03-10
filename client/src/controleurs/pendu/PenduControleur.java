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
import javafx.scene.layout.HBox;

public class PenduControleur extends client.src.controleurs.BaseControleur {
    private IPendu partie;
    private UUID id;

    @FXML
    private Group grp_pendu;
    @FXML
    private Button btn_jouer;
    @FXML
    private Button btn_lettre;
    @FXML
    private HBox hb_mot;
    @FXML
    private HBox hb_err;
    @FXML
    private TextField tf_lettre;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        try {
            this.partie = (IPendu) Naming.lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/pendu");
            btn_lettre.setDisable(true);
            tf_lettre.setDisable(true);
            hb_mot.setViewOrder(0);
            btn_jouer.setViewOrder(100);

            Platform.runLater(() -> {
                afficherPendu(11 - IPendu.MAX_VIE);
                _vue.setOnCloseRequest((event) -> {
                    quitter();
                });
            });

            tf_lettre.textProperty().addListener((ov, oldValue, newValue) -> {
                if (tf_lettre.getText().length() > 0) {
                    btn_lettre.setDisable(false);
                } else {
                    btn_lettre.setDisable(true);
                }

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
        tf_lettre.setDisable(false);
        afficherPendu(11 - IPendu.MAX_VIE);
        hb_err.getChildren().clear();
        this.id = partie.nouveauSalon();

        var indices = partie.recupIndice(this.id);
        hb_mot.getChildren().clear();
        hb_mot.setTranslateY(0);
        // En créant une variable ici, on évite de faire un requête au serveur pour
        // chaque itération
        int nbLettres = partie.recupNbLettres(this.id);
        for (int i = 0; i < nbLettres; i++) {
            Label lbl_placeholder = new Label("_");
            lbl_placeholder.setStyle("-fx-label-padding: 1;");
            hb_mot.getChildren().add(lbl_placeholder);
        }

        for (char c = 0; c <= 'z'; c++) {
            if (indices.get(c) != null) {
                for (int index : indices.get(c)) {
                    Label lbl_lettre = (Label) hb_mot.getChildren().get(index);
                    if (index == 0) {
                        lbl_lettre.setText(Character.toString(c).toUpperCase());
                    } else {
                        lbl_lettre.setText(Character.toString(c));
                    }
                }
            }
        }
    }

    public void handleLettre() throws RemoteException, InterruptedException {
        for (var element : hb_err.getChildren()) {
            Label lbl_err = (Label) element;
            if (lbl_err.getText().toLowerCase().equals(tf_lettre.getText().toLowerCase())) {
                tf_lettre.clear();
                return;
            }
        }
        var res = partie.envoiLettre(this.id, tf_lettre.getText().toLowerCase().charAt(0));
        afficherPendu(11 - res.getVie());
        if (res.getVie() == 0) {
            finir("Vous avez perdu...");
        } else if (res.getPositionLettre() != null) {
            boolean fini = true;
            for (int i = 0; i < hb_mot.getChildren().size(); i++) {
                Label lettre = (Label) hb_mot.getChildren().get(i);

                if (res.getPositionLettre().contains(i)) {
                    if (i == 0) {
                        lettre.setText(tf_lettre.getText().toUpperCase());
                    } else {
                        lettre.setText(tf_lettre.getText().toLowerCase());
                    }
                }

                if (lettre.getText().equals("_")) {
                    fini = false;
                }
            }

            if (fini) {
                finir("Vous avez gagné !");
            }
        }
        if (res.getPositionLettre() == null) {
            Label lbl_err = new Label(tf_lettre.getText().toLowerCase());
            lbl_err.setStyle("-fx-label-padding: 1;");
            hb_err.getChildren().add(lbl_err);
        }
        tf_lettre.clear();
    }

    public void finir(String texte) {
        hb_mot.getChildren().clear();
        Label lbl_fin = new Label(texte);
        hb_mot.getChildren().add(lbl_fin);
        hb_mot.setTranslateY(24);
        btn_jouer.setVisible(true);
        tf_lettre.setDisable(true);
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
