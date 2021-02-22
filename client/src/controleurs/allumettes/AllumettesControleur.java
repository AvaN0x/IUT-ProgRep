package client.src.controleurs.allumettes;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.src.controleurs.BaseControleur;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.UUID;

import client.src.ClientMain;
import commun.IAllumettes;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class AllumettesControleur extends BaseControleur {
    // TODO fix min size of window
    @FXML
    private Pane allumettesConteneur;

    private Group[] allumettes;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        initAllumettes();

        Platform.runLater(() -> {
            partieScriptee();
        });
    }

    private void initAllumettes() {
        int maxWidth = 400;
        int maxAllumettesLigne = maxWidth % (16 + 8);

        this.allumettes = new Group[35];
        for (int i = 0; i < 35; i++) {
            Rectangle tete = new Rectangle(16, 16);
            tete.setArcHeight(8);
            tete.setArcWidth(8);

            Rectangle tige = new Rectangle(8, 64);
            tige.setFill(Color.rgb(225, 225, 208));
            tige.setTranslateX(4);
            tete.setFill(Color.rgb(130, 39, 30));

            Group allumette = new Group();
            allumette.getChildren().addAll(tige, tete);
            allumette.setId(i + "");

            allumette.setOnMouseClicked((e) -> {
                var target = ((Shape) e.getTarget());
                System.out.println("target.getParent().getId() " + target.getParent().getId());

                if (e.getButton() == MouseButton.PRIMARY) {
                    ((Group) target.getParent()).getChildren().forEach((_node) -> {
                        ((Shape) _node).setEffect(new DropShadow(8, Color.RED));
                    });

                } else if (e.getButton() == MouseButton.SECONDARY) {
                    ((Group) target.getParent()).getChildren().forEach((_node) -> {
                        ((Shape) _node).setEffect(null);
                        // ((Shape) _node).setVisible(false);
                    });

                }
            });

            allumette.setLayoutX(4 + (i % maxAllumettesLigne) * (16 + 8));
            allumette.setLayoutY(4 + (i / maxAllumettesLigne) * (64 + 16));
            allumettesConteneur.getChildren().add(allumette);

            this.allumettes[i] = allumette;
        }
    }

    private void updateAllumettes(boolean[] allumettesServeur) {
        for (int i = 0; i < (allumettesServeur.length > this.allumettes.length ? this.allumettes.length
                : allumettesServeur.length); i++) {
            this.allumettes[i].setVisible(allumettesServeur[i]);
        }
    }

    private void partieScriptee() {
        try {
            IAllumettes partie = (IAllumettes) Naming
                    .lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/allumettes");

            UUID id = partie.nouveauSalon();

            while (partie.getNombreAllumettes(id) > 30) {
                if (partie.isAuJoueurDeJouer(id)) {
                    System.out.println("Le joueur joue avec -2 allumette");
                    // partie.retirer(id, 2);
                    partie.serveurJoue(id);
                } else {
                    System.out.println("Le serveur joue avec -1 allumette");
                    partie.serveurJoue(id);
                }
                System.out.println("Nombre d'allumettes restantes : " + partie.getNombreAllumettes(id));
                updateAllumettes(partie.getAllumettesArray(id));
                Thread.sleep(500);
            }
            System.out.println((partie.isAuJoueurDeJouer(id) ? "Le serveur" : "Le joueur") + " a gagné la partie !");

            partie.fermerSalon(id);
        } catch (Exception e) {
            showErreurAlerte("Allumettes exception: ", e.toString());
            this.fermer();
        }
    }

}
