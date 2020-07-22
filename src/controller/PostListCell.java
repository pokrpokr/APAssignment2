package controller;

import Exceptions.DBSaveException;
import Exceptions.ExistException;
import Exceptions.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import main.MainGUI;
import models.Event;
import models.Job;
import models.Post;
import models.Sale;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

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
            String basePath = "./";
            postID.setText(name);
            title.setText("title:"+post.getTitle());
            description.setText(post.getDescription());
            creatorId.setText("createdBy:"+post.getCreatorName());
            status.setText(post.getStatus());
            Image newImage = new Image(new File(basePath).toURI().toString() + post.getImage());
            imageView.setImage(newImage);

            if (name.indexOf("E") == 0) {
                label1.setText(((Event) post).getVenue());
                label2.setText(((Event) post).getDate());
                label3.setText("capacity:"+String.valueOf(((Event) post).getCapacity()));
                label4.setText("attCount:"+String.valueOf(((Event) post).getAttCount()));
                createReply.setText("join");
                anchorPane.setStyle("-fx-background-color: lightblue;");
            } else if (name.indexOf("S") == 0) {
                label1.setText("hPrice:"+String.valueOf(((Sale) post).getHighestOffer()));
                label2.setText("mRaise:"+String.valueOf(((Sale) post).getMinimumRaise()));
                label3.setText("");
                label4.setText("");
                anchorPane.setStyle("-fx-background-color: pink;");
            } else if (name.indexOf("J") == 0) {
                label1.setText("pPrice:"+String.valueOf(((Job) post).getProposedPrice()));
                label2.setText("lOffer:"+String.valueOf(((Job) post).getLowestOffer()));
                label3.setText("");
                label4.setText("");
                anchorPane.setStyle("-fx-background-color: grey;");
            }
            // Valite input and create reply
            createReply.setOnAction((event)->{
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("");
                    alert.initOwner(MainGUI.stage);
                    TextInputDialog d = new TextInputDialog();
                    d.setTitle("Creating Reply");
                    d.setHeaderText("");
                    d.initOwner(MainGUI.stage);
                    d.setContentText("Enter Your Offer: ");
                    if (post instanceof Event) {
                        try {
                            if(post.handleReply(1.0)){
                                alert.setContentText("Join Successfully!");
                            } else {
                                alert.setContentText("Join Failed!");
                            }
                        } catch (DBSaveException e) {
                            alert.setContentText(e.getMessage());
                        }
                        alert.show();
                    } else {
                        Optional<String> offer = d.showAndWait();
                        if (offer.isPresent()){
                            try {
                                Double offerD = Double.valueOf(offer.get());
                                if(post.handleReply(offerD)) {
                                    alert.setContentText("Reply Successfully!");
                                } else {
                                    alert.setContentText("Reply Failed!");
                                }
                            } catch (ValidationException e) {
                                alert.setContentText(e.toString());
                            } catch (Exception ee) {
                                alert.setContentText(ee.toString());
                            }
                            alert.show();
                        }
                    }
                    backToMainView(null);
                } catch (SQLException | ValidationException | ExistException | IOException e) {
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

    @FXML private void backToMainView(Post post) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
        Parent root = loader.load();
        MainViewController controller = loader.getController();
        controller.initialize(controller.currentUser, null);
        Scene scene = new Scene(root, 854, 600);
        MainGUI.stage.setScene(scene);
    }
}
