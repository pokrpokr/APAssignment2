package controller;

import Exceptions.ExistException;
import Exceptions.ValidationException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import main.MainGUI;
import models.Job;
import models.Post;
import models.Sale;
import models.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class CreateJobController {
    @FXML private TextField proposedPriceField;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private javafx.scene.control.MenuBar MenuBar;
    @FXML private Label ValidationMessage;
    @FXML private ImageView imageView;
    @FXML private User currentUser;
    @FXML private String imageUrl;

    @FXML public void initialize(User currentUser) {
        this.currentUser = currentUser;
        ValidationMessage.setText("");
        ValidationMessage.setTextFill(Color.RED);
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
    }

    public void UploadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D:/Uni Course related/AP/APAssignment2/images/"));
        File file = fileChooser.showOpenDialog(MainGUI.stage);
        if (file == null){
            ValidationMessage.setText("please upload image");
        } else {
            this.imageUrl = file.toURI().toString();
            Image newImage = new Image(this.imageUrl);
            imageView.setImage(newImage);
        }
    }

    public void CreateJob(ActionEvent actionEvent) {
        try {
            String idStr          = Job.generateId();
            if (idStr.isBlank()) { throw new ValidationException("ID did not generate"); }
            String title          = titleField.getText();
            String description    = descriptionField.getText();
            String proposedPrice  = proposedPriceField.getText();
            String imageUrl     = this.imageUrl;
            validation(new String[]{title, description, proposedPrice, imageUrl});
            if (currentUser == null) { throw new ValidationException("user logged out!"); }
            long creatorID     = currentUser.getId();
            String creatorName = currentUser.getUserName();
            Job job = new Job(creatorID, creatorName, idStr, title, description, imageUrl, Double.valueOf(proposedPrice));
            job = job.createJob(currentUser);
            backToMainView(job);
        } catch (ValidationException ve){
            ValidationMessage.setWrapText(true);
            ValidationMessage.setText(ve.getMessage());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExistException e) {
            e.printStackTrace();
        }
    }

    @FXML public void validation(String[] args) throws ValidationException {
//For checking date format
        String message = "";
        // 0: title
        if (args[0].isBlank()) { message += "title, "; }
        if (args[1].isBlank()) { message += "description, "; }
        if (args[2].isBlank()) { message += "proposedPrice, "; }
        if (args[3].isBlank()) { message += "image "; }
        if (!message.isEmpty()) { message += "can not be blank! "; }
        try{
            Double aPrice = Double.valueOf(args[2]);
            if (aPrice < 0) {
                throw new ValidationException("proposed price can not be less than 0!");
            }
        } catch (NumberFormatException e) {
            message += "proposed price should be a number!";
            throw new ValidationException(message);
        }
        if (!message.isBlank()){
            throw new ValidationException(message);
        }
    }

    @FXML private void backToMainView(Post post) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
        Parent root = loader.load();
        MainViewController controller = loader.getController();
        controller.initialize(currentUser, post);
        Scene scene = new Scene(root, 854, 600);
        MainGUI.stage.setScene(scene);
    }
}
