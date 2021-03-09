package serveur.src.controleurs.pendu;

import java.rmi.*;
import java.rmi.registry.*;

import serveur.src.modeles.pendu.*;

public class ServeurPendu {
    public ServeurPendu(String hote, int port) {
        try {
            LocateRegistry.createRegistry(port);

            Naming.rebind("rmi://" + hote + ":" + port + "/pendu", new Pendu());

            System.out.println(">> Serveur Pendu prêt !");
        } catch (Exception e) {
            System.out.println(">> Echec de connexion au serveur du pendu : " + e);
        }
    }
}
