package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.event.*;
import main.MainGUI;
import models.Post;
import models.User;

import java.io.IOException;

public class MainViewController {
    @FXML private Label currentUserName;
    @FXML private MenuItem devInfo;
    @FXML private MenuItem exitSys;
    private User currentUser = null;

    @FXML public void initialize(User currentUser,Post post){
        this.currentUser = currentUser;
        currentUserName.setText(currentUser.getUserName());
    }

    @FXML private void createNewEvent(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/create_event.fxml"));
            Parent root = loader.load();
            CreateEventController controller = loader.getController();
            controller.initialize(currentUser);
            Scene scene = new Scene(root, 486, 300);
            MainGUI.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML private void createNewSale(ActionEvent actionEvent) {
    }

    @FXML private void createNewJob(ActionEvent actionEvent) {
    }

    @FXML private void logoutMainView(ActionEvent actionEvent) {
    }
}
