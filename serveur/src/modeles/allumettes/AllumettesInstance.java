package serveur.src.modeles.allumettes;

import java.util.Arrays;

public class AllumettesInstance {
    private boolean[] allumettesRestantes;
    private boolean isAuJoueurDeJouer;

    public AllumettesInstance() {
        allumettesRestantes = new boolean[35];
        Arrays.fill(allumettesRestantes, true);
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
        isAuJoueurDeJouer = !isAuJoueurDeJouer;
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

    public void changerProchainJoueur() {
        this.isAuJoueurDeJouer = !this.isAuJoueurDeJouer;
    }

    public boolean isAuJoueurDeJouer() {
        return this.isAuJoueurDeJouer;
    }

}
