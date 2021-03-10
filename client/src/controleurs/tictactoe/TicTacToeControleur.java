package client.src.controleurs.tictactoe;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;

public class TicTacToeControleur extends client.src.controleurs.BaseControleur {
    @FXML
    private Pane pane_caseConteneur;

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        System.out.println("TicTacToeControleur");
        int[] cases = { 2, 1, 0, 1, 2, 0, 1, 0, 2 };
        reloadCases(cases);
    }

    public void quitter() {
        // try {
        // if (id != null)
        // this.partie.fermerSalon(this.id);
        // } catch (RemoteException e) {
        // showErreurAlerte("Allumettes exception: ", e.toString());
        // }
        this._vue.close();
    }

    private void reloadCases(int[] cases) {
        Platform.runLater(() -> {
            for (int i = 0; i < pane_caseConteneur.getChildren().size(); i++) {
                switch (cases[i]) {
                case 1:
                    addCroix((Group) pane_caseConteneur.getChildren().get(i));
                    break;
                case 2:
                    addRond((Group) pane_caseConteneur.getChildren().get(i));
                    break;
                default:
                    ((Group) pane_caseConteneur.getChildren().get(i)).getChildren().clear();
                    break;
                }
            }
        });
    }

    private void addCroix(Group grp) {
        Rectangle tige1 = new Rectangle(.1, .7);
        tige1.setFill(Color.rgb(0, 122, 204));
        tige1.setArcHeight(.1);
        tige1.setArcWidth(.1);
        tige1.setRotate(-45);
        Rectangle tige2 = new Rectangle(.10, .7);
        tige2.setFill(Color.rgb(0, 122, 204));
        tige2.setArcHeight(.1);
        tige2.setArcWidth(.1);
        tige2.setRotate(45);

        grp.getChildren().addAll(tige1, tige2);
    }

    private void addRond(Group grp) {
        Circle cercle = new Circle(.28, Color.TRANSPARENT);
        cercle.setStrokeWidth(.1);
        cercle.setStroke(Color.rgb(0, 122, 204));

        grp.getChildren().add(cercle);
    }

}
