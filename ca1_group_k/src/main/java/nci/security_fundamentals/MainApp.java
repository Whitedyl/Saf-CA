package nci.security_fundamentals;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;

// MainApp for JavaFX
public class MainApp extends Application {
    public Button myButton;

    @Override
    public void start(Stage stage) {
        // Simple UI to test JavaFX
        URL fxmlLocation = getClass().getResource("/views/builder.fxml");

        System.out.println("FXML Location: " + fxmlLocation);
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/builder.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

        }catch(Exception e){
            e.printStackTrace();
        }
//        Label label = new Label("Hello JavaFX with subpackages!");
//        VBox root = new VBox();
//        root.getChildren().add(label);
//
//
//
//        Scene scene = new Scene(root, 400, 200);
//        stage.setTitle("JavaFX App");
//        stage.setScene(scene);
//        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

