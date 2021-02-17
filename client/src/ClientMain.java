package client.src;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class ClientMain extends Application {
    public static int PORT = 6000;
    public static String HOTE = "127.0.0.1";

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlURL = getClass().getResource("vues/MainView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            Scene scene = new Scene((ScrollPane) fxmlLoader.load());
            scene.getStylesheets().add("vues/style.css");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Game server client");
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(320);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
