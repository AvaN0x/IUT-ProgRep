package client.src.vues.tictactoe;

import java.io.IOException;

import client.src.vues.BaseVue;

public class NewTicTacToeVue extends BaseVue {

    public NewTicTacToeVue() throws IOException {
        super("TicTacToeVue.fxml");
        this.setTitle("TicTacToe");
        this.setMinWidth(600);
        this.setMinHeight(400);
    }

}