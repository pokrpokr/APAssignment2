package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import main.MainGUI;
import models.Event;
import models.Job;
import models.Post;
import models.Sale;

import java.awt.*;
import java.io.IOException;

public class PostListCell extends ListCell<Post> {
    @FXML private ImageView imageView;
    @FXML private Label label4;
    @FXML private Label postID;
    @FXML private Label description;
    @FXML private Label creatorId;
    @FXML private Label label2;
    @FXML private Label status;
    @FXML private Label label1;
    @FXML private Label label3;
    @FXML private Label title;
    @FXML private Button createReply;
    @FXML private Button postDetails;
    @FXML private AnchorPane anchorPane;

    @Override
    protected void updateItem(Post post, boolean b) {
        super.updateItem(post, b);

        if (b || post == null) {
            setText(null);
            setGraphic(null);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/listCell_post.fxml"));
            loader.setController(this);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String name = post.getPostName();
            postID.setText(name);
            title.setText(post.getTitle());
            description.setText(post.getDescription());
            creatorId.setText(post.getCreatorName());
            status.setText(post.getStatus());
            Image newImage = new Image(post.getImage());
            imageView.setImage(newImage);

            if (name.indexOf("E") == 0) {
                label1.setText(((Event) post).getVenue());
                label2.setText(((Event) post).getDate());
                label3.setText(String.valueOf(((Event) post).getCapacity()));
                label4.setText(String.valueOf(((Event) post).getAttCount()));
                createReply.setText("join");
                anchorPane.setStyle("-fx-background-color: lightblue;");
            } else if (name.indexOf("S") == 0) {
                label1.setText(String.valueOf(((Sale) post).getHighestOffer()));
                label2.setText(String.valueOf(((Sale) post).getMinimumRaise()));
                label3.setText("");
                label4.setText("");
                anchorPane.setStyle("-fx-background-color: pink;");
            } else if (name.indexOf("J") == 0) {
                label1.setText(String.valueOf(((Job) post).getProposedPrice()));
                label2.setText(String.valueOf(((Job) post).getLowestOffer()));
                label3.setText("");
                label4.setText("");
                anchorPane.setStyle("-fx-background-color: yellowgreen;");
            }
            createReply.setOnAction((event)->{
                try {
                    FXMLLoader reply = new FXMLLoader(getClass().getResource("/view/create_reply.fxml"));
                    Parent root = reply.load();
                    CreateReplyController controller = reply.getController();
                    controller.initialize(post, MainViewController.currentUser);
                    Scene scene = new Scene(root, 486, 450);
                    MainGUI.stage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            if (post.getStatus().equals("CLOSED")) { createReply.setDisable(true); }
            if (post.getCreatorID() == MainViewController.currentUser.getId()){
                createReply.setDisable(true);
                postDetails.setOnAction((event)->{
                    try {
                        FXMLLoader details = new FXMLLoader(getClass().getResource("/view/post_details.fxml"));
                        Parent root = details.load();
                        PostDetailsController controller = details.getController();
                        controller.initialize(post, MainViewController.currentUser);
                        Scene scene = new Scene(root, 486, 480);
                        MainGUI.stage.setScene(scene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                postDetails.setVisible(false);
            }

            setText(null);
            setGraphic(anchorPane);
        }
    }
}
