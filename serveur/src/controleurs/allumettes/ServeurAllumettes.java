package serveur.src.controleurs.allumettes;

import java.rmi.*;
import java.rmi.registry.*;

import serveur.src.ServeurMain;
import serveur.src.modeles.allumettes.*;

public class ServeurAllumettes {
    public static void lancer() {
        try {
            LocateRegistry.createRegistry(ServeurMain.PORT);

            Naming.rebind("rmi://" + ServeurMain.HOTE + ":" + ServeurMain.PORT + "/allumettes", new Allumettes());

            System.out.println(">> Serveur Allumettes prêt !");
        } catch (Exception e) {
            System.out.println(">> Echec de connexion au serveur bancaire : " + e);
        }
    }
}
