package commun;

import java.io.Serializable;
import java.util.ArrayList;

public class PenduResultat implements Serializable {
    private int vie;
    private ArrayList<Integer> positionLettre;

    public PenduResultat(int vie, ArrayList<Integer> pos) {
        this.vie = vie;
        this.positionLettre = pos;
    }

    public int getVie() {
        return this.vie;
    }

    public ArrayList<Integer> getPositionLettre() {
        return this.positionLettre;
    }

}
