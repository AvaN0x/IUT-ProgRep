package serveur.src.modeles.allumettes;

import java.util.Arrays;

public class AllumettesInstance {
    private boolean[] allumettesRestantes;
    private boolean isAuJoueurDeJouer;

    public AllumettesInstance() {
        allumettesRestantes = new boolean[35];
        Arrays.fill(allumettesRestantes, true);
        isAuJoueurDeJouer = true;
    }

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

    public int getAleatPosition() {
        if (getNombreAllumettesRestantes() > 0) {
            int min = 0;
            int max = allumettesRestantes.length - 1;
            int i = -1;
            do {
                i = (int) (Math.random() * ((max - min) + 1)) + min;
            } while (!allumettesRestantes[i]);

            return i;
        }
        return -1;

    }

    /**
     * Change le prochain joueur dans le cas ou la partie n'est pas terminée
     * 
     * @param bool
     */
    public void setIsAuJoueurDeJouer(boolean bool) {
        if (getNombreAllumettesRestantes() > 0)
            this.isAuJoueurDeJouer = bool;
    }

    public boolean isAuJoueurDeJouer() {
        return this.isAuJoueurDeJouer;
    }

}
