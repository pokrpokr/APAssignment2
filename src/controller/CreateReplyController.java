package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import main.MainGUI;
import models.Post;
import models.User;

import java.io.IOException;

public class CreateReplyController {
    public void initialize(Post post, User currentUser) {
    }

    @FXML
    private void backToMainView(Post post) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
        Parent root = loader.load();
        MainViewController controller = loader.getController();
        controller.initialize(controller.currentUser, null);
        Scene scene = new Scene(root, 854, 600);
        MainGUI.stage.setScene(scene);
    }
}
