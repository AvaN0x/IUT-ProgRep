package client.src.controleurs.allumettes;

import java.net.URL;
import java.util.ResourceBundle;

import client.src.controleurs.BaseControleur;

import java.rmi.Naming;
import java.util.UUID;

import client.src.ClientMain;
import commun.IAllumettes;

public class AllumettesControleur extends BaseControleur {

    @Override
    public void initialize(URL location, ResourceBundle ressources) {
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
