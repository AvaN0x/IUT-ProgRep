package commun;

public class PenduResultat {
    private int vie;
    private int positionLettre;

    public PenduResultat(int vie, int pos) {
        this.vie = vie;
        this.positionLettre = pos;
    }

    public int getVie() {
        return this.vie;
    }

    public int getPositionLettre() {
        return this.positionLettre;
    }

}
