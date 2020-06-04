package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.event.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.MainGUI;
import models.Post;
import models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MainViewController {
    @FXML private ChoiceBox typeList;
    @FXML private ChoiceBox statusList;
    @FXML private ChoiceBox creatorList;
    @FXML private ListView<Post> postLists;
    @FXML private Label currentUserName;
    @FXML private MenuItem devInfo;
    @FXML private MenuItem exitSys;
    public static User currentUser = null;

    @FXML public void initialize(User currentUser,Post post){
        HashMap<String,String> options = new HashMap<>();
        typeList.setValue("All");
        statusList.setValue("All");
        creatorList.setValue("All");
        typeList.setItems(FXCollections.observableList((List) Post.getTypes()));
        statusList.setItems(FXCollections.observableList((List) Post.getStatusA()));
        ArrayList<String> creators = new ArrayList<>();
        creators.add("All");
        creators.add("MyPost");
        creatorList.setItems(FXCollections.observableList((List) creators));
        MainViewController.currentUser = currentUser;
        currentUserName.setText(currentUser.getUserName());
        typeList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observableValue, str, t1) -> {
            if (t1.equals("All")) {
                options.remove("type");
            } else {
                options.put("type", t1);
            }
            List<Post> posts = Post.getPosts(options);
            ObservableList<Post> list = FXCollections.observableList(posts);
            postLists.setItems(list);
            postLists.setCellFactory(postListView -> new PostListCell());
        });
        statusList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observableValue, str, t1) -> {
            if (t1.equals("All")) {
                options.remove("status");
            } else {
                options.put("status", t1);
            }
            List<Post> posts = Post.getPosts(options);
            ObservableList<Post> list = FXCollections.observableList(posts);
            postLists.setItems(list);
            postLists.setCellFactory(postListView -> new PostListCell());
        });
        creatorList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observableValue, str, t1) -> {
            if (t1.equals("All")) {
                options.remove("creatorId");
            } else {
                options.put("creatorId", String.valueOf(currentUser.getId()));
            }
            List<Post> posts = Post.getPosts(options);
            ObservableList<Post> list = FXCollections.observableList(posts);
            postLists.setItems(list);
            postLists.setCellFactory(postListView -> new PostListCell());
        });
        List<Post> posts = Post.getPosts(options);
        ObservableList<Post> list = FXCollections.observableList(posts);
        postLists.setItems(list);
        postLists.setCellFactory(postListView -> new PostListCell());

        devInfo.setOnAction((event)->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Developer Info");
            alert.setHeaderText("");
            alert.setContentText("Student Number: s3766925\nStudent Name: Xingyu Chai");
            alert.initOwner(MainGUI.stage);
            alert.show();
        });

        exitSys.setOnAction((event)->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit System");
            alert.setHeaderText("");
            alert.setContentText("Are you sure to exit System?");
            alert.initOwner(MainGUI.stage);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {System.exit(0);}
            }
        });
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
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/create_job.fxml"));
            Parent root = loader.load();
            CreateJobController controller = loader.getController();
            controller.initialize(currentUser);
            Scene scene = new Scene(root, 486, 300);
            MainGUI.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void logoutMainView(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_view.fxml"));
            Parent root = loader.load();
            currentUser = null;
            LoginViewController controller = new LoginViewController();
            Scene scene = new Scene(root, 480, 200);
            MainGUI.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
