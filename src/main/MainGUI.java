package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login_view.fxml"));
            Scene scene = new Scene(root, 480, 200);
            stage.setTitle("UniLink System");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to load fxml file");
        }
    }
}
