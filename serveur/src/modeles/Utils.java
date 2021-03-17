package serveur.src.modeles;

import java.io.*;
import java.net.*;

public class Utils {
    public static int randomInt(int min, int max) {
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    public static String getUrlContents(String theUrl) {
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            // Se connecte à l'URL
            URLConnection con = url.openConnection();
            // Récupère le contenu de l'url via des readers
            BufferedReader liseur = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String ligne;
            // Tant que l'url récupérée contient une ligne
            while ((ligne = liseur.readLine()) != null) {
                // On l'ajoute au résultat
                res.append(ligne + "\n");
            }
            liseur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}
