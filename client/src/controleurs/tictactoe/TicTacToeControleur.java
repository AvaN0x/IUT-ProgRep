package client.src.controleurs.tictactoe;

import java.net.URL;
import java.util.ResourceBundle;

public class TicTacToeControleur extends client.src.controleurs.BaseControleur {

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
        System.out.println("TicTacToeControleur");
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

}
