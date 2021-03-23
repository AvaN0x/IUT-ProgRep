package serveur.src.modeles.allumettes;

import java.util.Arrays;

import commun.IAllumettes;
import serveur.src.modeles.Utils;

public class AllumettesInstance {
    private boolean[] allumettesRestantes;

    private int nombreAllumettesJoueur;
    private int nombreAllumettesServeur;

    private int premierCoup;

    public AllumettesInstance() {
        allumettesRestantes = new boolean[Utils.randomInt(3, 12) * 2 + 1];
        Arrays.fill(allumettesRestantes, true);
        nombreAllumettesJoueur = 0;
        nombreAllumettesServeur = 0;
        premierCoup = Utils.randomInt(IAllumettes.JOUEUR, IAllumettes.SERVEUR);
    }

    /**
     * Compte le nombre de d'allumettes pass récupérée
     * 
     * @return nombre d'allumettes
     */
    public int getNombreAllumettesRestantes() {
        int i = 0;
        for (boolean b : allumettesRestantes) {
            if (b)
                i++;
        }
        return i;
    }

    public boolean[] getAllumettesArray() {
        return allumettesRestantes;
    }

    public void retirer(int position) {
        this.allumettesRestantes[position] = false;
    }

    /***
     * Récupère une position aléatoire disponible
     * 
     * @return position
     */
    public int getAleatPosition() {
        if (getNombreAllumettesRestantes() > 0) {
            int min = 0;
            int max = allumettesRestantes.length - 1;
            int i = -1;
            do {
                i = Utils.randomInt(min, max);
            } while (!allumettesRestantes[i]);

            return i;
        }
        return -1;

    }

    public int getNombreAllumettesJoueur() {
        return this.nombreAllumettesJoueur;
    }

    public void addNombreAllumettesJoueur(int nombre) {
        this.nombreAllumettesJoueur += nombre;
    }

    public int getNombreAllumettesServeur() {
        return this.nombreAllumettesServeur;
    }

    public void addNombreAllumettesServeur(int nombre) {
        this.nombreAllumettesServeur += nombre;
    }

    public int getPremierCoup() {
        return this.premierCoup;
    }

}
