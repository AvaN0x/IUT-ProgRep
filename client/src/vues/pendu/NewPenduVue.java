package client.src.vues.pendu;

import java.io.IOException;

import client.src.vues.BaseVue;

public class NewPenduVue extends BaseVue {

    public NewPenduVue() throws IOException {
        super("PenduVue.fxml");
        this.setTitle("Pendu");
        this.setMinWidth(600);
        this.setMinHeight(400);
    }

}