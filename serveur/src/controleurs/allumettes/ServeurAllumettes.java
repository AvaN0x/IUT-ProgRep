package serveur.src.controleurs.allumettes;

import java.rmi.*;
import java.rmi.registry.*;

import serveur.src.modeles.allumettes.*;

public class ServeurAllumettes {
    public ServeurAllumettes(String hote, int port) {
        // ? Essayer de créer le registre, s'il est déjà crée une erreur aura lieu, mais
        // ? le serveur sera toujours configuré
        // ? Cette exception arrive si un autre serveur a déjà été lancé avant
        try {
            LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
        }
        try {
            Naming.rebind("rmi://" + hote + ":" + port + "/allumettes", new Allumettes());

            System.out.println(">> Serveur Allumettes prêt !");
        } catch (Exception e) {
            System.out.println(">> Echec de connexion au serveur du jeux des allumettes : " + e);
        }
    }
}
