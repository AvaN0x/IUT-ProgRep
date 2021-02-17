package client.src.vues.allumettes;

import java.rmi.Naming;
import java.util.UUID;

import commun.IAllumettes;

public class Allumettes {

    public static void main(String[] args) {
        try {
            int port = 6000;
            String hote = "127.0.0.1";

            IAllumettes partie = (IAllumettes) Naming.lookup("rmi://" + hote + ":" + port + "/allumettes");

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
            System.out.println("Allumettes exception: " + e);
        }
    }
}