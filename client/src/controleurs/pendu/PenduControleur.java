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
import commun.PenduResultat;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
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
            // On se connecte au serveur
            this.partie = (IPendu) Naming.lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/pendu");
            // On désactive l'entrée utilisateur
            btn_lettre.setDisable(true);
            tf_lettre.setDisable(true);

            // On affiche l'état initial du pendu
            Platform.runLater(() -> {
                afficherPendu(11 - IPendu.MAX_VIE);
                _vue.setOnCloseRequest((event) -> {
                    quitter();
                });
            });

            tf_lettre.textProperty().addListener((ov, oldValue, newValue) -> {
                // Si la nouvelle valeur de l'entrée utilisateur est vide
                if (newValue.length() <= 0) {
                    // On désactive le bouton pour envoyler la lettre au serveur
                    btn_lettre.setDisable(true);
                } else {
                    // On active le bouton pour envoyler la lettre au serveur
                    btn_lettre.setDisable(false);
                    if (newValue.length() > 1) {
                        // On limite l'entrée utilisateur à 1 caractère
                        String s = newValue.substring(0, 1);
                        tf_lettre.setText(s);
                    } else if (!newValue.matches("[A-zÀ-ú]")) {
                        // On empêche l'entrée utilisateur de contenir des caractère spéciaux
                        String s = newValue.substring(0, 0);
                        tf_lettre.setText(s);
                    }
                }

            });

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            showErreurAlerte("Pendu exception: ", e.toString());
            this.fermer();
        }
    }

    /**
     * Au clic du bouton "Jouer", on initialise la partie
     * 
     * @throws RemoteException
     */
    public void initPartie() throws RemoteException {
        // On cache le bouton de lancement de partie
        btn_jouer.setVisible(false);
        // On active le TextField responsable de l'entrée utilisateur
        tf_lettre.setDisable(false);
        // On affiche l'état initial du pendu (pour le remettre à 0 après une partie)
        afficherPendu(11 - IPendu.MAX_VIE);
        // On efface tout le contenu précédent dans le conteneur des lettres erronées
        hb_err.getChildren().clear();
        // On efface tout le contenu précédent dans le conteneur des lettres / message
        // de fin
        hb_mot.getChildren().clear();
        // On créer un nouveau salon
        this.id = partie.nouveauSalon();

        // En créant une variable ici, on évite de faire un requête au serveur pour
        // chaque itération
        var indices = partie.recupIndice(this.id);
        // En créant une variable ici, on évite de faire un requête au serveur pour
        // chaque itération
        int nbLettres = partie.recupNbLettres(this.id);
        // Pour chaque lettre du mot...
        for (int i = 0; i < nbLettres; i++) {
            // ... on rajoute un tiret pour indiquer qu'une lettre existe
            Label lbl_placeholder = new Label("_");
            lbl_placeholder.setStyle("-fx-label-padding: 1;");
            hb_mot.getChildren().add(lbl_placeholder);
        }

        // Pour tout l'alphabet...
        for (char c = 0; c <= 'z'; c++) {
            // ... si la lettre fait partie des indices
            if (indices.get(c) != null) {
                // Pour toutes les occurences de la lettre dans les indices
                for (int index : indices.get(c)) {
                    // On récupère le label à la position de la lettre
                    Label lbl_lettre = (Label) hb_mot.getChildren().get(index);
                    // On écrit la lettre (en majuscule si c'est la première lettre du mot)
                    if (index == 0) {
                        lbl_lettre.setText(Character.toString(c).toUpperCase());
                    } else {
                        lbl_lettre.setText(Character.toString(c));
                    }
                }
            }
        }
    }

    /**
     * Au clic du bouton "Envoyer lettre"
     * 
     * @throws RemoteException
     * @throws InterruptedException
     */
    public void handleLettre() throws RemoteException, InterruptedException {
        // Pour chaque lettre érronée
        for (Node element : hb_err.getChildren()) {
            Label lbl_err = (Label) element;
            // Si la lettre envoyée par l'utilisateur existe (et est donc érronée)
            if (lbl_err.getText().toLowerCase().equals(tf_lettre.getText().toLowerCase())) {
                // On empêche le joueur de perdre inutilement de la vie
                tf_lettre.clear();
                return;
            }
        }
        // On demande au serveur si la lettre est valide
        PenduResultat res = partie.envoiLettre(this.id, tf_lettre.getText().toLowerCase().charAt(0));
        // On met à jour la vie du joueur
        afficherPendu(11 - res.getVie());
        // Si le joueur n'a plus de vie
        if (res.getVie() == 0) {
            // On finit la partie
            finir("Vous avez perdu...");
            // Si la lettre est valide
        } else if (res.getPositionLettre() != null) {
            // Variable pour savoir si le mot est deviné
            boolean fini = true;
            // Pour chaque label dans le conteneur
            for (int i = 0; i < hb_mot.getChildren().size(); i++) {
                Label lettre = (Label) hb_mot.getChildren().get(i);

                // On vérifie si la position du label est la même que celle de la lettre trouvée
                if (res.getPositionLettre().contains(i)) {
                    // Et on l'affiche en majuscule si c'est la première lettre du mot
                    if (i == 0) {
                        lettre.setText(tf_lettre.getText().toUpperCase());
                    } else {
                        lettre.setText(tf_lettre.getText().toLowerCase());
                    }
                }
                // Si il reste des lettres a deviner...
                if (lettre.getText().equals("_")) {
                    fini = false;
                }
            }

            // Si le mot est deviné
            if (fini) {
                // On fini la partie
                finir("Vous avez gagné !");
            }
        }
        // Si la lettre n'existe pas dans le mot
        if (res.getPositionLettre() == null) {
            // On rajoutte la lettre au conteneur d'erreurs
            Label lbl_err = new Label(tf_lettre.getText().toLowerCase());
            lbl_err.setStyle("-fx-label-padding: 1;");
            hb_err.getChildren().add(lbl_err);
        }
        tf_lettre.clear();
    }

    /**
     * Remet l'interface à son point initial en affichant un texte à l'écran
     * 
     * @param texte Le texte a afficher
     */
    public void finir(String texte) {
        // On retire les lettres du mot
        hb_mot.getChildren().clear();
        // On affiche le message de fin
        Label lbl_fin = new Label(texte);
        hb_mot.getChildren().add(lbl_fin);
        // On affiche le bouton "Jouer"
        btn_jouer.setVisible(true);
        // On désactive l'entrée utilisateur
        tf_lettre.setDisable(true);
        // On ferme le salon
        try {
            if (id != null)
                this.partie.fermerSalon(this.id);
        } catch (RemoteException e) {
            showErreurAlerte("Pendu exception: ", e.toString());
        }
    }

    /**
     * Au clic du bouton "Quitter"
     */
    public void quitter() {
        // On ferme le salon
        try {
            if (id != null)
                this.partie.fermerSalon(this.id);
        } catch (RemoteException e) {
            showErreurAlerte("Pendu exception: ", e.toString());
        }
        // On ferme la fenêtre
        this._vue.close();
    }

    /**
     * Dessine le pendu
     * 
     * @param stade Le stade du pendu
     */
    private void afficherPendu(int stade) {
        Platform.runLater(() -> {
            for (int i = 0; i < grp_pendu.getChildren().size(); i++)
                grp_pendu.getChildren().get(i).setVisible(i < stade);
        });
    }

}
