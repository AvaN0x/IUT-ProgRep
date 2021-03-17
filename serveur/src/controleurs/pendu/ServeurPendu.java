package serveur.src.controleurs.pendu;

import java.rmi.*;
import java.rmi.registry.*;

import serveur.src.modeles.pendu.*;

public class ServeurPendu {
    public ServeurPendu(String hote, int port) {
        // Essayer de créer le registre, s'il est déjà crée une erreur aura lieu, mais
        // le serveur sera toujours configuré
        // Cette exception arrive si un autre serveur a déjà été lancé avant
        try {
            LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
        }
        try {
            Naming.rebind("rmi://" + hote + ":" + port + "/pendu", new Pendu());

            System.out.println(">> Serveur Pendu prêt !");
        } catch (Exception e) {
            System.out.println(">> Echec de connexion au serveur du pendu : " + e);
        }
    }
}
