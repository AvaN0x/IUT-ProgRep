package commun;

import java.io.Serializable;

public class PenduResultat implements Serializable {
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
