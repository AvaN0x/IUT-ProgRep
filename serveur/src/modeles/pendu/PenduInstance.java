package serveur.src.modeles.pendu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class PenduInstance {
    private String mot;
    private int vie = 5;

    /**
     * Configure un nouveau salon pour un joueur de pendu.
     * 
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public PenduInstance() throws ParserConfigurationException, SAXException, IOException {
        // TODO: A tester sous Eclipse
        File fichier = new File("serveur\\src\\modeles\\pendu\\pendu.xml");
        DocumentBuilderFactory usine = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructeur = usine.newDocumentBuilder();
        Document doc = constructeur.parse(fichier);
        // On analyse le fichier contenant les mots
        doc.getDocumentElement().normalize();
        // On récupère la liste des mots
        NodeList motsNoeudListe = doc.getElementsByTagName("Mots").item(0).getChildNodes();

        ArrayList<String> motsPossibles = new ArrayList<>();
        for (int i = 0; i < motsNoeudListe.getLength(); i++) {
            // Pour chaque Node dans la liste on vérifie qu'il y a bien un mot et on le
            // rajoutte à une liste
            Node noeudMot = motsNoeudListe.item(i);
            if (noeudMot != null && noeudMot.getNodeType() == Node.ELEMENT_NODE) {
                motsPossibles.add(noeudMot.getTextContent());
            }
        }

        // On choisi un mot aléatoire parmis la liste des mots
        Random alea = new Random();
        int numMot = alea.nextInt(motsPossibles.size() + 1);
        this.mot = motsPossibles.get(numMot);
    }

    /**
     * Récupère le mot en entier
     * 
     * @return le mot
     */
    public String recupererMot() {
        return this.mot;
    }

    /**
     * Récupère la vie du joueur
     * 
     * @return la vie du joueur
     */
    public int recupererVie() {
        return this.vie;
    }

    /**
     * Fait perdre de la vie au joueur
     * 
     * @return la nouvelle vide du joueur
     */
    public int perdreVie() {
        this.vie--;
        return this.vie;
    }

    public HashMap<Integer, Character> recupIndice() {
        HashMap<Integer, Character> map = new HashMap<>();
        for (int i = 0; i < this.mot.length() / 5; i++) {
            Random alea = new Random();
            int numLettre = alea.nextInt(this.mot.length() + 1);
            map.put(numLettre, this.mot.charAt(numLettre));
        }
        return map;
    }

}
