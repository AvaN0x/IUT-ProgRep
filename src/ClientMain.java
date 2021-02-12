import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlURL = getClass().getResource("views/MainView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            Scene scene = new Scene((ScrollPane) fxmlLoader.load());
            scene.getStylesheets().add("views/style.css");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Game server");
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
