package controller;

import Exceptions.ExistException;
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
import models.Event;
import models.Post;
import models.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class CreateEventController {
    @FXML private TextArea descriptionField;
    @FXML private Label ValidationMessage;
    @FXML private javafx.scene.control.MenuBar MenuBar;
    @FXML private TextField venueField;
    @FXML private TextField dateField;
    @FXML private TextField capacityField;
    @FXML private TextField titleField;
    @FXML private ImageView imageView;
    @FXML private String imageUrl = "";
    private User currentUser = null;

    @FXML public void initialize(User currentUser){
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

    @FXML private void UploadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./images/"));
        File file = fileChooser.showOpenDialog(MainGUI.stage);
        if (file == null){
            ValidationMessage.setText("please upload image");
        } else {
            String basePath = "./";
            this.imageUrl = new File(basePath).toURI().relativize(file.toURI()).getPath();
            Image newImage = new Image( new File(basePath).toURI().toString() + this.imageUrl);
            imageView.setImage(newImage);
        }
    }

    @FXML private void CreateEvent(ActionEvent actionEvent) {
        try {
            String idStr       = Event.generateId();
            if (idStr.isBlank()) { throw new ValidationException("ID did not generate"); }
            String title       = titleField.getText();
            String description = descriptionField.getText();
            String date        = dateField.getText();
            String venue       = venueField.getText();
            String capacity    = capacityField.getText();
            String imageUrl    = this.imageUrl;
            validation(new String[]{title, description, venue, date, capacity, imageUrl});
            if (currentUser == null) { throw new ValidationException("user logged out!"); }
            long creatorID     = currentUser.getId();
            String creatorName = currentUser.getUserName();
            Event event = new Event(creatorID, creatorName, idStr, title, description, imageUrl, venue, date, Integer.valueOf(capacity));
            event = event.createEvent(currentUser);
            backToMainView(event);
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

    // Validate input string
    @FXML public void validation(String[] args) throws ValidationException {
        //For checking date format
        String regex = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)(?:0?2|(?:Feb))\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
        String message = "";
        // 0: title
        if (args[0].isBlank()) { message += "title, "; }
        if (args[1].isBlank()) { message += "description, "; }
        if (args[2].isBlank()) { message += "venue, "; }
        if (args[3].isBlank()) { message += "date, "; }
        if (args[4].isBlank()) { message += "capacity, "; }
        if (args[5].isBlank()) { message += "image "; }
        if (!message.isEmpty()) { message += "can not be blank! "; }
        System.out.println(args[3].length());
        if (!args[3].matches(regex))
        {
            message += "date format wrong!";
        }
        try{
            Integer.valueOf(args[4]);
        } catch (NumberFormatException e) {
            message += "capacity should be a number!";
            throw new ValidationException(message);
        }
        if (Integer.valueOf(args[4]) <= 0){
            message += "capacity should greater than 0!";
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
