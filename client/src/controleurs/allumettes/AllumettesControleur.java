package client.src.controleurs.allumettes;

import java.net.URL;
import java.util.ResourceBundle;

import client.src.controleurs.BaseControleur;

import java.rmi.Naming;
import java.util.UUID;

import client.src.ClientMain;
import commun.IAllumettes;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class AllumettesControleur extends BaseControleur {
    @FXML
    private Pane allumettesConteneur;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        for (int i = 0; i < 3; i++) {
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
                    });

                }
            });

            allumettesConteneur.getChildren().add(allumette);

        }

        // partieScriptee();
    }

    private void partieScriptee() {
        try {
            IAllumettes partie = (IAllumettes) Naming
                    .lookup("rmi://" + ClientMain.HOTE + ":" + ClientMain.PORT + "/allumettes");

            UUID id = partie.nouveauSalon();

            while (partie.getNombreAllumettes(id) > 0) {
                if (partie.isAuJoueurDeJouer(id)) {
                    System.out.println("Le joueur joue avec -2 allumette");
                    partie.retirer(id, 2);
                } else {
                    System.out.println("Le serveur joue avec -1 allumette");
                    partie.serveurJoue(id);
                }
                System.out.println("Nombre d'allumettes restantes : " + partie.getNombreAllumettes(id));
            }
            System.out.println((partie.isAuJoueurDeJouer(id) ? "Le serveur" : "Le joueur") + " a gagné la partie !");

            partie.fermerSalon(id);
        } catch (Exception e) {
            showErreurAlerte("Allumettes exception: ", e.toString());
            this.fermer();
        }
    }

}
