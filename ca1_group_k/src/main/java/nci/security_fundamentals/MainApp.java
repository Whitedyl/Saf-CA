package nci.security_fundamentals;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// MainApp for JavaFX
public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // Simple UI to test JavaFX
        Label label = new Label("Hello JavaFX with subpackages!");

        VBox root = new VBox(100);
        root.getChildren().add(label);



        Scene scene = new Scene(root, 400, 200);
        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

