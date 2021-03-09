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
            this.partie = (IAllumettes) Naming
                    .lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/allumettes");

            initLobby();

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

    private void initLobby() throws RemoteException {
        initLobby("");
    }

    private void initLobby(String logString) throws RemoteException {
        id = null;
        Platform.runLater(() -> {
            allumettesConteneur.getChildren().clear();
            btn_jouer.setVisible(false);
            grp_joueurAllumettes.setVisible(false);
            grp_serveurAllumettes.setVisible(false);
            setLog(logString);

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

            btn_lancerPartie.requestFocus();
        });
    }

    private void initPartie() throws RemoteException, InterruptedException {
        this.id = partie.nouveauSalon();

        allumettesSelectionnee = new ArrayList<Integer>();

        allumettesConteneur.getChildren().clear();
        btn_jouer.setVisible(true);
        grp_joueurAllumettes.setVisible(true);
        grp_serveurAllumettes.setVisible(true);
        lbl_nombreAllumettesJoueur.setText("0");
        lbl_nombreAllumettesServeur.setText("0");
        setLog("");

        btn_jouer.setDisable(true);

        int nbAllumettes = partie.getNombreAllumettes(id);
        int maxWidth = 400;
        int maxAllumettesLigne = maxWidth % (16 + 8);
        int lignesNecessaires = nbAllumettes / maxAllumettesLigne;

        this.allumettes = new Group[nbAllumettes];
        for (int i = 0; i < nbAllumettes; i++) {
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

            allumette.setTranslateX(4 + (i % maxAllumettesLigne) * (16 + 8) - maxWidth / 2);
            allumette.setTranslateY(4 + (i / maxAllumettesLigne) * (64 + 16) - (lignesNecessaires * (64 + 16)) / 2);

            allumette.setOnMouseClicked((e) -> handleAllumetteClick(e));
            allumettesConteneur.getChildren().add(allumette);

            this.allumettes[i] = allumette;
        }

        isAuJoueurDeJouer = partie.quiCommence(id) == IAllumettes.JOUEUR;
        if (!isAuJoueurDeJouer) {
            setLog("Le serveur commence à jouer.");
            serveurJoue(1200);
        } else {
            setLog("A vous de commencer à jouer.");
        }
    }

    private void handleAllumetteClick(MouseEvent event) throws ClassCastException {
        if (isAuJoueurDeJouer) {
            var target = ((Shape) event.getTarget());
            int id = Integer.parseInt(target.getParent().getId());

            if (event.getButton() == MouseButton.PRIMARY) {
                if (!isAllumetteSelectionnee(id) && allumettesSelectionnee.size() < 2) {
                    ((Group) target.getParent()).getChildren().forEach((shape) -> {
                        ((Shape) shape).setEffect(new DropShadow(8, Color.RED));
                    });
                    allumettesSelectionnee.add(id);
                }

            } else if (event.getButton() == MouseButton.SECONDARY) {
                if (isAllumetteSelectionnee(id)) {
                    ((Group) target.getParent()).getChildren().forEach((_node) -> {
                        ((Shape) _node).setEffect(null);
                    });
                    allumettesSelectionnee.remove((Object) id);
                }
            }
            btn_jouer.setDisable(allumettesSelectionnee.size() == 0);
        }
    }

    private boolean isAllumetteSelectionnee(int id) {
        for (int a : allumettesSelectionnee)
            if (a == id)
                return true;
        return false;
    }

    private void updateAllumettes(boolean[] allumettesServeur) {
        for (int i = 0; i < (allumettesServeur.length > this.allumettes.length ? this.allumettes.length
                : allumettesServeur.length); i++) {
            this.allumettes[i].setVisible(allumettesServeur[i]);
        }
    }

    public void quitter() {
        try {
            if (id != null)
                this.partie.fermerSalon(this.id);
        } catch (RemoteException e) {
            showErreurAlerte("Allumettes exception: ", e.toString());
        }
        this._vue.close();
    }

    public void jouer() throws RemoteException {
        if (allumettesSelectionnee.size() > 0) {
            partie.jouer(id, allumettesSelectionnee);
            allumettesSelectionnee.clear();
            updateAllumettes(partie.getAllumettesArray(id));
        }
        isAuJoueurDeJouer = false;
        btn_jouer.setDisable(true);

        if (verifierFinDePartie())
            return;

        serveurJoue();
    }

    private void serveurJoue() {
        serveurJoue(500);
    }

    private void serveurJoue(int timer) {
        new Thread(() -> {
            try {
                Thread.sleep(timer);
                int nombreAllumettesPrise = partie.serveurJoue(id);
                setLog("Le serveur a prit " + nombreAllumettesPrise + " allumettes!");

                updateAllumettes(partie.getAllumettesArray(id));
                isAuJoueurDeJouer = true;

                verifierFinDePartie();
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean verifierFinDePartie() throws RemoteException {
        int nombreAllumettesJoueur = partie.getNombreAllumettesJoueur(id);
        int nombreAllumettesServeur = partie.getNombreAllumettesServeur(id);
        Platform.runLater(() -> {
            lbl_nombreAllumettesJoueur.setText("" + nombreAllumettesJoueur);
            lbl_nombreAllumettesServeur.setText("" + nombreAllumettesServeur);
        });
        if (partie.getNombreAllumettes(id) <= 0) {
            initLobby((partie.getNombreAllumettesJoueur(id) % 2 == 1 ? "Vous avez" : "Le serveur a") + " gagné.");
            partie.fermerSalon(id);
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
