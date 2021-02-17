package serveur.src.controleurs.allumettes;

import java.rmi.*;
import java.rmi.registry.*;

import serveur.src.modeles.allumettes.*;

public class ServeurAllumettes {
    public static void lancer(String hote, int port) {
        try {
            LocateRegistry.createRegistry(port);

            Naming.rebind("rmi://" + hote + ":" + port + "/allumettes", new Allumettes());

            System.out.println(">> Serveur Allumettes prêt !");
        } catch (Exception e) {
            System.out.println(">> Echec de connexion au serveur bancaire : " + e);
        }
    }
}
