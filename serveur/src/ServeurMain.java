package serveur.src;

import serveur.src.controleurs.allumettes.ServeurAllumettes;
import serveur.src.controleurs.pendu.ServeurPendu;

public class ServeurMain {
    public static int PORT = 6000;
    public static String HOTE = "127.0.0.1";

    public static void main(String[] args) {
        System.out.println(">> Lancement du serveur principal.");
        new ServeurAllumettes(HOTE, PORT);
        // new ServeurPendu(HOTE, PORT);
    }
}
