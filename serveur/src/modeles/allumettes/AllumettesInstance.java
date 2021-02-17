package serveur.src.modeles.allumettes;

public class AllumettesInstance {
    private int allumettesRestantes;
    private boolean isAuJoueurDeJouer;

    public AllumettesInstance() {
        allumettesRestantes = 21;
    }

    public int getAllumettesRestantes() {
        return this.allumettesRestantes;
    }

    public void setAllumettesRestantes(int allumettesRestantes) {
        this.allumettesRestantes = allumettesRestantes;
    }

    public void retirer(int quantite) {
        this.allumettesRestantes -= quantite;
        isAuJoueurDeJouer = !isAuJoueurDeJouer;
    }

    public boolean isAuJoueurDeJouer() {
        return this.isAuJoueurDeJouer;
    }

}
