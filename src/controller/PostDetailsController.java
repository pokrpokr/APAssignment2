package controller;

import Exceptions.ValidationException;
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
        Image loadImage = new Image(this.imageUrl);
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

        } else if (post instanceof Job) {

        }
    }

    public void UploadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D:/Uni Course related/AP/APAssignment2/images/"));
        File file = fileChooser.showOpenDialog(MainGUI.stage);
        if (file == null && this.imageUrl.isBlank()){
            ValidationMessage.setText("please upload image");
        } else {
            this.imageUrl = file.toURI().toString();
            Image newImage = new Image(this.imageUrl);
            imageView.setImage(newImage);
        }
    }

    public void SavePost(ActionEvent actionEvent) {
        String title       = post.getTitle();
        String description = post.getDescription();
        if (!title.equals(titleField.getText())) { title = titleField.getText(); }
        if (!description.equals(descriptionField.getText())) { description = titleField.getText(); }
        if (post instanceof Event) {
            String date        = ((Event) post).getDate();
            if (!date.equals(label3Field.getText())) { date = label3Field.getText(); }
            String venue       = ((Event) post).getVenue();
            if (!venue.equals(label2Field.getText())) { venue = label2Field.getText(); }
            String capacity    = String.valueOf(((Event) post).getCapacity());
            if (!capacity.equals(label1Field.getText())) { capacity = label1Field.getText(); }
            String imageUrl    = post.getImage();
            if (!imageUrl.equals(this.imageUrl)) { imageUrl = this.imageUrl; }
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

        } else if (post instanceof Job) {

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
