package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;

public class MainViewController {
    @FXML private ListView<Post> postLists;
    @FXML private Label currentUserName;
    @FXML private MenuItem devInfo;
    @FXML private MenuItem exitSys;
    public static User currentUser = null;

    @FXML public void initialize(User currentUser,Post post){
        MainViewController.currentUser = currentUser;
        currentUserName.setText(currentUser.getUserName());
        List<Post> posts = Post.getPosts();
        ObservableList<Post> list = FXCollections.observableList(posts);
        postLists.setItems(list);
        postLists.setCellFactory(postListView -> new PostListCell());
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
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/create_sale.fxml"));
            Parent root = loader.load();
            CreateSaleController controller = loader.getController();
            controller.initialize(currentUser);
            Scene scene = new Scene(root, 486, 300);
            MainGUI.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void createNewJob(ActionEvent actionEvent) {
    }

    @FXML private void logoutMainView(ActionEvent actionEvent) {
    }
}
