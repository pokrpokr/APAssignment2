package controller;

import Exceptions.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import main.MainGUI;
import models.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PostDetailsController {
    @FXML private Label label2;
    @FXML private Label label3;
    @FXML private Label label1;
    @FXML private Label label4;
    @FXML private Label label4Content;
    @FXML private TextField label1Field;
    @FXML private TextField label2Field;
    @FXML private TextField label3Field;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private javafx.scene.control.MenuBar MenuBar;
    @FXML private Label ValidationMessage;
    @FXML private ImageView imageView;
    @FXML private ListView replyDetails;
    @FXML private Post post;
    @FXML private String imageUrl;
    @FXML private User currentUser;

    @FXML public void initialize(Post post, User currentUser) {
        String basePath = "./";
        ValidationMessage.setText("");
        ValidationMessage.setTextFill(Color.RED);
        this.currentUser = currentUser;
        Label menuLabel = new Label("BackToMainView");
        menuLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    backToMainView(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Menu backButton = new Menu();
        backButton.setGraphic(menuLabel);
        MenuBar.getMenus().add(backButton);
        this.post = post;
        this.imageUrl = post.getImage();
        Image loadImage = new Image(new File(basePath).toURI().toString() + this.imageUrl);
        imageView.setImage(loadImage);
        titleField.setText(post.getTitle());
        descriptionField.setText(post.getDescription());
        if (post instanceof Event) {
            label1.setText("capacity:");
            label1Field.setText(String.valueOf(((Event) post).getCapacity()));
            label2.setText("venue:");
            label2Field.setText(((Event) post).getVenue());
            label3.setText("date:");
            label3Field.setText(((Event) post).getDate());
            label4.setText("attendees:");
            label4Content.setText(String.valueOf(((Event) post).getAttCount()));
        } else if (post instanceof Sale) {
            label1.setText("askingPrice:");
            label1Field.setText(String.valueOf(((Sale) post).getAskingPrice()));
            label2.setText("minimumRaise:");
            label2Field.setText(String.valueOf(((Sale) post).getMinimumRaise()));
            label3.setVisible(false);
            label3Field.setVisible(false);
            label4.setText("highestOffer:");
            label4Content.setText(String.valueOf(((Sale) post).getHighestOffer()));
        } else if (post instanceof Job) {
            label1.setText("proposedPrice:");
            label1Field.setText(String.valueOf(((Job) post).getProposedPrice()));
            label2.setVisible(false);
            label2Field.setVisible(false);
            label3.setVisible(false);
            label3Field.setVisible(false);
            label4.setText("lowestOffer:");
            label4Content.setText(String.valueOf(((Job) post).getLowestOffer()));
        }

        try {
            ObservableList<String> list = FXCollections.observableList((List<String>) post.getReplies());
            replyDetails.setItems(list);
        } catch (SQLException e) {
            ValidationMessage.setWrapText(true);
            ValidationMessage.setText(e.getMessage());
        }
    }

    public void UploadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./images/"));
        File file = fileChooser.showOpenDialog(MainGUI.stage);
        if (file == null && this.imageUrl.isBlank()){
            ValidationMessage.setText("please upload image");
        } else {
            String basePath = "./";
            this.imageUrl = new File(basePath).toURI().relativize(file.toURI()).getPath();
            Image newImage = new Image( new File(basePath).toURI().toString() + this.imageUrl);
            imageView.setImage(newImage);
        }
    }

    public void SavePost(ActionEvent actionEvent) {
        String title       = post.getTitle();
        String description = post.getDescription();
        if (!title.equals(titleField.getText())) { title = titleField.getText(); }
        if (!description.equals(descriptionField.getText())) { description = titleField.getText(); }
        if (post instanceof Event) {
            String date        = label3Field.getText();
            String venue       = label2Field.getText();
            String capacity    = label1Field.getText();
            String imageUrl    = this.imageUrl;
            CreateEventController cE = new CreateEventController();
            try {
                cE.validation(new String[]{title, description, venue, date, capacity, imageUrl});
                if (currentUser == null) { throw new ValidationException("user logged out!"); }
                String sql = "UPDATE posts SET title = '"
                        + title +"', description = '"+ description +"', venue = '"+ venue +"', date = '"+ date +"', imageUrl = '"+ imageUrl +"', capacity = " + Integer.valueOf(capacity)
                        + " WHERE id = " + post.getPostID();
                if (Post.updatePost(sql)){
                    backToMainView(post);
                }
            } catch (ValidationException | SQLException | IOException e) {
                ValidationMessage.setWrapText(true);
                ValidationMessage.setText(e.getMessage());
            }
        } else if (post instanceof Sale) {
            String askingPrice  = label1Field.getText();
            String minimumRaise = label2Field.getText();
            CreateSaleController cS = new CreateSaleController();
            try {
                cS.validation(new String[]{title, description, askingPrice, minimumRaise, imageUrl});
                if (currentUser == null) { throw new ValidationException("user logged out!"); }
                if (Double.valueOf(askingPrice) < ((Sale) post).getHighestOffer()) { post.setClose(); }
                String sql = "UPDATE posts SET title = '"
                        + title +"', description = '"+ description +"', askingPrice = "+ Double.valueOf(askingPrice) +", minimumRaise = "+ minimumRaise +", imageUrl = '"+ imageUrl +"',"
                        + " status = '" + post.getStatus() + "'" + " WHERE id = " + post.getPostID();
                if (Post.updatePost(sql)){
                    backToMainView(post);
                }
            } catch (ValidationException | SQLException | IOException e) {
                ValidationMessage.setWrapText(true);
                ValidationMessage.setText(e.getMessage());
            }
        } else if (post instanceof Job) {
            String proposedPrice = label1Field.getText();
            String lowestOffer = String.valueOf(((Job) post).getLowestOffer());
            CreateJobController cJ = new CreateJobController();
            try {
                cJ.validation(new String[]{title, description, proposedPrice, imageUrl});
                if (currentUser == null) { throw new ValidationException("user logged out!"); }
                if (((Job) post).getProposedPrice() == ((Job) post).getLowestOffer()) { lowestOffer = proposedPrice; }
                String sql = "UPDATE posts SET title = '"
                        + title +"', description = '"+ description +"', proposedPrice = "+ Double.valueOf(proposedPrice) +", "+ "lowestOffer = "+ Double.valueOf(lowestOffer) +", imageUrl = '"+ imageUrl + "'"
                        + " WHERE id = " + post.getPostID();
                if (Post.updatePost(sql)){
                    backToMainView(post);
                }
            } catch (ValidationException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void ClosePost(ActionEvent actionEvent) {
        try {
            if (post.closePost()){
                backToMainView(null);
            }
        } catch (SQLException | IOException throwables) {
            ValidationMessage.setText("Error Occur");
        }
    }

    public void DeletePost(ActionEvent actionEvent) {
        try {
            if(post.deletePost()) {
                backToMainView(null);
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML private void backToMainView(Post post) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
        Parent root = loader.load();
        MainViewController controller = loader.getController();
        controller.initialize(currentUser, null);
        Scene scene = new Scene(root, 854, 600);
        MainGUI.stage.setScene(scene);
    }
}
