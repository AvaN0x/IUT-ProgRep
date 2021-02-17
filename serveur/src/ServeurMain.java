package serveur.src;

import serveur.src.controleurs.allumettes.ServeurAllumettes;

public class ServeurMain {

    public static void main(String[] args) {
        System.out.println(">> Lancement du serveur principal.");
        int port = 6000;
        String hote = "localhost";
        ServeurAllumettes.lancer(hote, port);
    }
}
