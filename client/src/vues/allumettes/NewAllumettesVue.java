package client.src.vues.allumettes;

import java.io.IOException;

import client.src.vues.BaseVue;

public class NewAllumettesVue extends BaseVue {

    public NewAllumettesVue() throws IOException {
        super("AllumettesVue.fxml");
        this.setTitle("Jeux des allumettes");
        this.setMinWidth(300);
        this.setMinHeight(200);
    }

}