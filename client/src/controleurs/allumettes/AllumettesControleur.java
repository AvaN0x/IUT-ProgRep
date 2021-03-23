package client.src.controleurs.allumettes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.UUID;

import client.src.ClientMain;
import commun.IAllumettes;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class AllumettesControleur extends client.src.controleurs.BaseControleur {
    private IAllumettes partie;
    private UUID id;

    private Group[] allumettes;
    private ArrayList<Integer> allumettesSelectionnee;

    private boolean isAuJoueurDeJouer;

    @FXML
    private StackPane allumettesConteneur;
    @FXML
    private Button btn_jouer;
    @FXML
    private Label lbl_logServeurJoue;
    @FXML
    private Label lbl_nombreAllumettesJoueur;
    @FXML
    private Label lbl_nombreAllumettesServeur;
    @FXML
    private Group grp_joueurAllumettes;
    @FXML
    private Group grp_serveurAllumettes;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        try {
            // On se connecte a userveur
            this.partie = (IAllumettes) Naming
                    .lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/allumettes");

            // On initialise le lobby
            initLobby("");

            // Evenement lors du clic de la croix de la fenetre
            Platform.runLater(() -> {
                _vue.setOnCloseRequest((event) -> {
                    quitter();
                });
            });

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            showErreurAlerte("Allumettes exception: ", e.toString());
            this.fermer();
        }
    }

    private void initLobby(String logString) throws RemoteException {
        Platform.runLater(() -> {
            // On enlève toutes les allumettes
            allumettesConteneur.getChildren().clear();
            // On cache les données non nécessaires sur le lobby
            btn_jouer.setVisible(false);
            grp_joueurAllumettes.setVisible(false);
            grp_serveurAllumettes.setVisible(false);
            setLog(logString);

            // On ajoute un bouton permettant d'initialiser une partie
            var btn_lancerPartie = new Button("Lancer la partie");
            btn_lancerPartie.setAlignment(Pos.CENTER);
            btn_lancerPartie.setOnAction((event) -> {
                try {
                    initPartie();
                } catch (RemoteException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            allumettesConteneur.getChildren().add(btn_lancerPartie);

            // On demande a ce que le bouton soit selectionné
            btn_lancerPartie.requestFocus();
        });
    }

    private void initPartie() throws RemoteException, InterruptedException {
        // On récupère l'id du nouveau salon
        this.id = partie.nouveauSalon();

        allumettesSelectionnee = new ArrayList<Integer>();

        // Initialise les compostants de la page
        allumettesConteneur.getChildren().clear();
        btn_jouer.setVisible(true);
        grp_joueurAllumettes.setVisible(true);
        grp_serveurAllumettes.setVisible(true);
        lbl_nombreAllumettesJoueur.setText("0");
        lbl_nombreAllumettesServeur.setText("0");
        setLog("");

        btn_jouer.setDisable(true);

        // On récupère le nombre d'allumettes
        int nbAllumettes = partie.getNombreAllumettes(id);
        // On fait quelques calcules pour savoir comment on va afficher les allumettes
        int maxWidth = 400;
        int maxAllumettesLigne = maxWidth % (16 + 8);
        int lignesNecessaires = nbAllumettes / maxAllumettesLigne;

        // On initialise un tableau contenant tous les groupes (element JavaFX) des
        // allumettes
        this.allumettes = new Group[nbAllumettes];
        for (int i = 0; i < nbAllumettes; i++) {
            // On dessine l'allumette
            Rectangle tete = new Rectangle(16, 16);
            tete.setFill(Color.rgb(130, 39, 30));
            tete.setArcHeight(8);
            tete.setArcWidth(8);

            Rectangle tige = new Rectangle(8, 64);
            tige.setFill(Color.rgb(225, 225, 208));
            tige.setTranslateX(4);
            tige.setArcHeight(8);
            tige.setArcWidth(8);

            Group allumette = new Group();
            allumette.getChildren().addAll(tige, tete);
            allumette.setId(i + "");

            // On positionne l'allumette
            allumette.setTranslateX(4 + (i % maxAllumettesLigne) * (16 + 8) - maxWidth / 2);
            allumette.setTranslateY(4 + (i / maxAllumettesLigne) * (64 + 16) - (lignesNecessaires * (64 + 16)) / 2);

            // Fonction de clic sur une allumette
            allumette.setOnMouseClicked((e) -> handleAllumetteClick(e));
            // On ajoute l'allumette à la fenetre JavaFX
            allumettesConteneur.getChildren().add(allumette);

            this.allumettes[i] = allumette;
        }

        // On affiche un message différent en fonction de qui commence
        isAuJoueurDeJouer = partie.quiCommence(id) == IAllumettes.JOUEUR;
        if (!isAuJoueurDeJouer) {
            setLog("Le serveur commence à jouer.");
            // On demande au serveur de jouer
            serveurJoue(1200);
        } else {
            setLog("A vous de commencer à jouer.");
        }
    }

    private void handleAllumetteClick(MouseEvent event) throws ClassCastException {
        // Le clic sur une allumette ne fonctionne que si c'est au joueur de jouer
        if (isAuJoueurDeJouer) {
            // On récupère l'id de l'allumette cliquée en fonction de son parent (groupe)
            var target = ((Shape) event.getTarget());
            int id = Integer.parseInt(target.getParent().getId());

            if (event.getButton() == MouseButton.PRIMARY) {
                // Un clic gauche, on selectionne l'allumette
                if (!isAllumetteSelectionnee(id) && allumettesSelectionnee.size() < 2) {
                    ((Group) target.getParent()).getChildren().forEach((shape) -> {
                        ((Shape) shape).setEffect(new DropShadow(8, Color.RED));
                    });
                    allumettesSelectionnee.add(id);
                }

            } else if (event.getButton() == MouseButton.SECONDARY) {
                // Un clic droit, on déselectionne l'allumette
                if (isAllumetteSelectionnee(id)) {
                    ((Group) target.getParent()).getChildren().forEach((_node) -> {
                        ((Shape) _node).setEffect(null);
                    });
                    allumettesSelectionnee.remove((Object) id);
                }
            }

            // On rend cliquable ou non le bouton jouer, il faut au minimum une allumette
            // selectionnée
            btn_jouer.setDisable(allumettesSelectionnee.size() == 0);
        }
    }

    private boolean isAllumetteSelectionnee(int id) {
        // On parcours les allumettes selectionnées pour savoir si l'allumette est déjà
        // selectionnée
        for (int a : allumettesSelectionnee)
            if (a == id)
                return true;
        return false;
    }

    private void updateAllumettes(boolean[] allumettesServeur) {
        // On affiche ou cache les allumettes en fonction de leur status
        for (int i = 0; i < (allumettesServeur.length > this.allumettes.length ? this.allumettes.length
                : allumettesServeur.length); i++) {
            this.allumettes[i].setVisible(allumettesServeur[i]);
        }
    }

    public void quitter() {
        try {
            // Si l'id n'est pas null, on ferme le salon
            if (this.id != null) {
                this.partie.fermerSalon(this.id);
                id = null;
            }
        } catch (RemoteException e) {
            showErreurAlerte("Allumettes exception: ", e.toString());
        }
        this._vue.close();
    }

    public void jouer() throws RemoteException {
        // On ne peut jouer que si le nombre d'allumettes selectionnées est superieur a
        if (allumettesSelectionnee.size() > 0) {
            partie.jouer(id, allumettesSelectionnee);
            allumettesSelectionnee.clear();
            updateAllumettes(partie.getAllumettesArray(id));
        }
        isAuJoueurDeJouer = false;
        btn_jouer.setDisable(true);

        // On vérifie si la partie est terminée
        if (verifierFinDePartie())
            return;

        // On demande au serveur de jouer
        serveurJoue(500);
    }

    private void serveurJoue(int timer) {
        new Thread(() -> {
            try {
                // On attend le temps en ms passé en paramètres
                Thread.sleep(timer);
                int nombreAllumettesPrise = partie.serveurJoue(id);
                setLog("Le serveur a prit " + nombreAllumettesPrise + " allumettes!");

                updateAllumettes(partie.getAllumettesArray(id));
                isAuJoueurDeJouer = true;

                // On verifie si la partie est terminée
                verifierFinDePartie();
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean verifierFinDePartie() throws RemoteException {
        // On récupère les nombres d'allumettes
        int nombreAllumettesJoueur = partie.getNombreAllumettesJoueur(id);
        int nombreAllumettesServeur = partie.getNombreAllumettesServeur(id);
        // On met à jour les labels
        Platform.runLater(() -> {
            lbl_nombreAllumettesJoueur.setText("" + nombreAllumettesJoueur);
            lbl_nombreAllumettesServeur.setText("" + nombreAllumettesServeur);
        });

        // S'il n'y a plus d'allumettes, alors la partie est terminée et on ferme le
        // salon
        if (partie.getNombreAllumettes(id) <= 0) {
            var joueurGagne = partie.getNombreAllumettesJoueur(id) % 2 == 1;
            partie.fermerSalon(id);
            id = null;
            initLobby((joueurGagne ? "Vous avez" : "Le serveur a") + " gagné.");
            return true;
        } else
            return false;
    }

    private void setLog(String logString) {
        Platform.runLater(() -> {
            lbl_logServeurJoue.setText(logString);
        });
    }
}
